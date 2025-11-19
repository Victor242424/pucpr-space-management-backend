package dev.victor_rivas.space_management.controller;

import dev.victor_rivas.space_management.model.dto.ApiResponse;
import dev.victor_rivas.space_management.model.dto.OccupancyReportDTO;
import dev.victor_rivas.space_management.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/occupancy")
    public ResponseEntity<ApiResponse<List<OccupancyReportDTO>>> getOccupancyReport() {
        List<OccupancyReportDTO> report = reportService.getOccupancyReport();
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @GetMapping("/occupancy/space/{spaceId}")
    public ResponseEntity<ApiResponse<OccupancyReportDTO>> getOccupancyReportBySpace(
            @PathVariable Long spaceId) {
        OccupancyReportDTO report = reportService.getOccupancyReportBySpace(spaceId);
        return ResponseEntity.ok(ApiResponse.success(report));
    }
}