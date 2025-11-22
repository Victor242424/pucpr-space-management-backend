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
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccessRecordService {

    private final AccessRecordRepository accessRecordRepository;
    private final StudentRepository studentRepository;
    private final SpaceRepository spaceRepository;
    private final MetricsService metricsService;

    @Transactional
    @Timed(value = "space.entry.register", description = "Time to register an entry")
    public AccessRecordDTO registerEntry(EntryRequest request) {
        return metricsService.getEntryRegistrationTimer().record(() -> {
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

            List<AccessRecord> studentActiveAccesses = accessRecordRepository
                    .findByStudentAndStatus(student, AccessStatus.ACTIVE);

            if (!studentActiveAccesses.isEmpty()) {
                throw new BusinessException("Student already has an active access in a space");
            }

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

            if (currentOccupancy + 1 >= space.getCapacity() || space.getStatus() == SpaceStatus.AVAILABLE) {
                space.setStatus(SpaceStatus.OCCUPIED);
                spaceRepository.save(space);
            }

            // Registrar métrica
            metricsService.recordEntry();

            return convertToDTO(accessRecord);
        });
    }

    @Transactional
    @Timed(value = "space.exit.register", description = "Time to register an exit")
    public AccessRecordDTO registerExit(ExitRequest request) {
        return metricsService.getExitRegistrationTimer().record(() -> {
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

            Space space = accessRecord.getSpace();
            Long currentOccupancy = accessRecordRepository.countActiveAccessBySpace(space);

            if (currentOccupancy == 0) {
                space.setStatus(SpaceStatus.AVAILABLE);
            } else if (currentOccupancy < space.getCapacity()) {
                space.setStatus(SpaceStatus.OCCUPIED);
            }
            spaceRepository.save(space);

            // Registrar métrica
            metricsService.recordExit();

            return convertToDTO(accessRecord);
        });
    }

    @Transactional(readOnly = true)
    @Timed(value = "access.records.get.all", description = "Time to get all access records")
    public List<AccessRecordDTO> getAllAccessRecords() {
        return accessRecordRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    @Timed(value = "access.records.get.by.student", description = "Time to get access records by student")
    public List<AccessRecordDTO> getAccessRecordsByStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        return accessRecordRepository.findByStudent(student).stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    @Timed(value = "access.records.get.by.space", description = "Time to get access records by space")
    public List<AccessRecordDTO> getAccessRecordsBySpace(Long spaceId) {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Space not found"));

        return accessRecordRepository.findBySpace(space).stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    @Timed(value = "access.records.get.active", description = "Time to get active access records")
    public List<AccessRecordDTO> getActiveAccessRecords() {
        return accessRecordRepository.findByStatus(AccessStatus.ACTIVE).stream()
                .map(this::convertToDTO)
                .toList();
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
