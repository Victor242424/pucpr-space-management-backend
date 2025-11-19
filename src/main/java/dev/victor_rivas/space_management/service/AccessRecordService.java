package dev.victor_rivas.space_management.service;

import dev.victor_rivas.space_management.enums.AccessStatus;
import dev.victor_rivas.space_management.enums.SpaceStatus;
import dev.victor_rivas.space_management.enums.StudentStatus;
import dev.victor_rivas.space_management.exception.BusinessException;
import dev.victor_rivas.space_management.exception.ResourceNotFoundException;
import dev.victor_rivas.space_management.model.dto.AccessRecordDTO;
import dev.victor_rivas.space_management.model.dto.EntryRequest;
import dev.victor_rivas.space_management.model.dto.ExitRequest;
import dev.victor_rivas.space_management.model.entity.AccessRecord;
import dev.victor_rivas.space_management.model.entity.Space;
import dev.victor_rivas.space_management.model.entity.Student;
import dev.victor_rivas.space_management.repository.AccessRecordRepository;
import dev.victor_rivas.space_management.repository.SpaceRepository;
import dev.victor_rivas.space_management.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccessRecordService {

    private final AccessRecordRepository accessRecordRepository;
    private final StudentRepository studentRepository;
    private final SpaceRepository spaceRepository;

    @Transactional
    public AccessRecordDTO registerEntry(EntryRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (student.getStatus() != StudentStatus.ACTIVE) {
            throw new BusinessException("Student is not active");
        }

        Space space = spaceRepository.findById(request.getSpaceId())
                .orElseThrow(() -> new ResourceNotFoundException("Space not found"));

        if (space.getStatus() != SpaceStatus.AVAILABLE &&
                space.getStatus() != SpaceStatus.OCCUPIED) {
            throw new BusinessException("Space is not available");
        }

        // Check if student already has an active access to this space
        accessRecordRepository.findActiveAccessByStudentAndSpace(student, space)
                .ifPresent(ar -> {
                    throw new BusinessException("Student already has an active access to this space");
                });

        // Check space capacity
        Long currentOccupancy = accessRecordRepository.countActiveAccessBySpace(space);
        if (currentOccupancy >= space.getCapacity()) {
            throw new BusinessException("Space has reached maximum capacity");
        }

        AccessRecord accessRecord = AccessRecord.builder()
                .student(student)
                .space(space)
                .entryTime(LocalDateTime.now())
                .status(AccessStatus.ACTIVE)
                .notes(request.getNotes())
                .build();

        accessRecord = accessRecordRepository.save(accessRecord);

        // Update space status if needed
        if (currentOccupancy + 1 >= space.getCapacity()) {
            space.setStatus(SpaceStatus.OCCUPIED);
            spaceRepository.save(space);
        } else if (space.getStatus() == SpaceStatus.AVAILABLE) {
            space.setStatus(SpaceStatus.OCCUPIED);
            spaceRepository.save(space);
        }

        return convertToDTO(accessRecord);
    }

    @Transactional
    public AccessRecordDTO registerExit(ExitRequest request) {
        AccessRecord accessRecord = accessRecordRepository.findById(request.getAccessRecordId())
                .orElseThrow(() -> new ResourceNotFoundException("Access record not found"));

        if (accessRecord.getStatus() != AccessStatus.ACTIVE) {
            throw new BusinessException("Access record is not active");
        }

        accessRecord.setExitTime(LocalDateTime.now());
        accessRecord.setStatus(AccessStatus.COMPLETED);

        if (request.getNotes() != null) {
            accessRecord.setNotes(accessRecord.getNotes() != null ?
                    accessRecord.getNotes() + " | " + request.getNotes() :
                    request.getNotes());
        }

        accessRecord = accessRecordRepository.save(accessRecord);

        // Update space status
        Space space = accessRecord.getSpace();
        Long currentOccupancy = accessRecordRepository.countActiveAccessBySpace(space);

        if (currentOccupancy == 0) {
            space.setStatus(SpaceStatus.AVAILABLE);
        } else if (currentOccupancy < space.getCapacity()) {
            space.setStatus(SpaceStatus.OCCUPIED);
        }
        spaceRepository.save(space);

        return convertToDTO(accessRecord);
    }

    @Transactional(readOnly = true)
    public List<AccessRecordDTO> getAllAccessRecords() {
        return accessRecordRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AccessRecordDTO> getAccessRecordsByStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        return accessRecordRepository.findByStudent(student).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AccessRecordDTO> getAccessRecordsBySpace(Long spaceId) {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Space not found"));

        return accessRecordRepository.findBySpace(space).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AccessRecordDTO> getActiveAccessRecords() {
        return accessRecordRepository.findByStatus(AccessStatus.ACTIVE).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private AccessRecordDTO convertToDTO(AccessRecord accessRecord) {
        return AccessRecordDTO.builder()
                .id(accessRecord.getId())
                .studentId(accessRecord.getStudent().getId())
                .studentName(accessRecord.getStudent().getName())
                .studentRegistrationNumber(accessRecord.getStudent().getRegistrationNumber())
                .spaceId(accessRecord.getSpace().getId())
                .spaceName(accessRecord.getSpace().getName())
                .spaceCode(accessRecord.getSpace().getCode())
                .entryTime(accessRecord.getEntryTime().toString())
                .exitTime(accessRecord.getExitTime() != null ?
                        accessRecord.getExitTime().toString() : null)
                .durationInMinutes(accessRecord.getDurationInMinutes())
                .status(accessRecord.getStatus())
                .notes(accessRecord.getNotes())
                .createdAt(accessRecord.getCreatedAt().toString())
                .build();
    }
}
