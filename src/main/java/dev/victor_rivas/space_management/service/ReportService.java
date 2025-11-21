package dev.victor_rivas.space_management.service;

import dev.victor_rivas.space_management.exception.ResourceNotFoundException;
import dev.victor_rivas.space_management.model.dto.OccupancyReportDTO;
import dev.victor_rivas.space_management.model.entity.AccessRecord;
import dev.victor_rivas.space_management.model.entity.Space;
import dev.victor_rivas.space_management.repository.AccessRecordRepository;
import dev.victor_rivas.space_management.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final SpaceRepository spaceRepository;
    private final AccessRecordRepository accessRecordRepository;

    @Transactional(readOnly = true)
    public List<OccupancyReportDTO> getOccupancyReport() {
        List<Space> spaces = spaceRepository.findAll();

        return spaces.stream()
                .map(this::generateOccupancyReport)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OccupancyReportDTO getOccupancyReportBySpace(Long spaceId) {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Space not found"));

        return generateOccupancyReport(space);
    }

    private OccupancyReportDTO generateOccupancyReport(Space space) {
        Long currentOccupancy = accessRecordRepository.countActiveAccessBySpace(space);
        Double occupancyRate = (currentOccupancy.doubleValue() / space.getCapacity()) * 100;

        LocalDateTime startOfToday = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endOfToday = LocalDateTime.now().with(LocalTime.MAX);
        List<AccessRecord> todayRecords = accessRecordRepository
                .findBySpaceAndEntryTimeBetween(space, startOfToday, endOfToday);

        LocalDateTime startOfWeek = LocalDateTime.now().minusDays(7);
        List<AccessRecord> weekRecords = accessRecordRepository
                .findBySpaceAndEntryTimeBetween(space, startOfWeek, LocalDateTime.now());

        LocalDateTime startOfMonth = LocalDateTime.now().minusDays(30);
        List<AccessRecord> monthRecords = accessRecordRepository
                .findBySpaceAndEntryTimeBetween(space, startOfMonth, LocalDateTime.now());

        Double averageDuration = monthRecords.stream()
                .filter(ar -> ar.getDurationInMinutes() != null)
                .mapToLong(AccessRecord::getDurationInMinutes)
                .average()
                .orElse(0.0);

        return OccupancyReportDTO.builder()
                .spaceId(space.getId())
                .spaceName(space.getName())
                .spaceCode(space.getCode())
                .capacity(space.getCapacity())
                .currentOccupancy(currentOccupancy.intValue())
                .occupancyRate(Math.round(occupancyRate * 100.0) / 100.0)
                .totalAccessesToday((long) todayRecords.size())
                .totalAccessesThisWeek((long) weekRecords.size())
                .totalAccessesThisMonth((long) monthRecords.size())
                .averageDurationInMinutes(Math.round(averageDuration * 100.0) / 100.0)
                .build();
    }
}

