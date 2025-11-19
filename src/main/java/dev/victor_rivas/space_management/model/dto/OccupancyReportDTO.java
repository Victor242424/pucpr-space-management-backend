package dev.victor_rivas.space_management.model.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OccupancyReportDTO {
    private Long spaceId;
    private String spaceName;
    private String spaceCode;
    private Integer capacity;
    private Integer currentOccupancy;
    private Double occupancyRate;
    private Long totalAccessesToday;
    private Long totalAccessesThisWeek;
    private Long totalAccessesThisMonth;
    private Double averageDurationInMinutes;
}