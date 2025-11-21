package dev.victor_rivas.space_management.service;

import dev.victor_rivas.space_management.enums.SpaceStatus;
import dev.victor_rivas.space_management.exception.BusinessException;
import dev.victor_rivas.space_management.exception.ResourceNotFoundException;
import dev.victor_rivas.space_management.model.dto.SpaceDTO;
import dev.victor_rivas.space_management.model.entity.Space;
import dev.victor_rivas.space_management.repository.AccessRecordRepository;
import dev.victor_rivas.space_management.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpaceService {

    private final SpaceRepository spaceRepository;
    private final AccessRecordRepository accessRecordRepository;

    @Transactional
    public SpaceDTO createSpace(SpaceDTO spaceDTO) {
        if (spaceRepository.existsByCode(spaceDTO.getCode())) {
            throw new BusinessException("Space code already exists");
        }

        Space space = Space.builder()
                .code(spaceDTO.getCode())
                .name(spaceDTO.getName())
                .type(spaceDTO.getType())
                .capacity(spaceDTO.getCapacity())
                .building(spaceDTO.getBuilding())
                .floor(spaceDTO.getFloor())
                .description(spaceDTO.getDescription())
                .status(SpaceStatus.AVAILABLE)
                .build();

        space = spaceRepository.save(space);
        return convertToDTO(space);
    }

    @Transactional(readOnly = true)
    public List<SpaceDTO> getAllSpaces() {
        return spaceRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SpaceDTO getSpaceById(Long id) {
        Space space = spaceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Space not found with id: " + id));
        return convertToDTO(space);
    }

    @Transactional
    public SpaceDTO updateSpace(Long id, SpaceDTO spaceDTO) {
        Space space = spaceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Space not found with id: " + id));

        if (!space.getCode().equals(spaceDTO.getCode()) &&
                spaceRepository.existsByCode(spaceDTO.getCode())) {
            throw new BusinessException("Space code already exists");
        }

        space.setCode(spaceDTO.getCode());
        space.setName(spaceDTO.getName());
        space.setType(spaceDTO.getType());
        space.setCapacity(spaceDTO.getCapacity());
        space.setBuilding(spaceDTO.getBuilding());
        space.setFloor(spaceDTO.getFloor());
        space.setDescription(spaceDTO.getDescription());

        if (spaceDTO.getStatus() != null) {
            space.setStatus(spaceDTO.getStatus());
        }

        space = spaceRepository.save(space);
        return convertToDTO(space);
    }

    @Transactional
    public void deleteSpace(Long id) {
        Space space = spaceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Space not found with id: " + id));

        if (accessRecordRepository.existsBySpaceId(id)){
            space.setStatus(SpaceStatus.UNAVAILABLE);
            spaceRepository.save(space);
        } else {
            spaceRepository.deleteById(id);
        }
    }

    private SpaceDTO convertToDTO(Space space) {
        Long currentOccupancy = accessRecordRepository.countActiveAccessBySpace(space);

        return SpaceDTO.builder()
                .id(space.getId())
                .code(space.getCode())
                .name(space.getName())
                .type(space.getType())
                .capacity(space.getCapacity())
                .building(space.getBuilding())
                .floor(space.getFloor())
                .description(space.getDescription())
                .status(space.getStatus())
                .currentOccupancy(currentOccupancy.intValue())
                .createdAt(space.getCreatedAt().toString())
                .updatedAt(space.getUpdatedAt().toString())
                .build();
    }
}