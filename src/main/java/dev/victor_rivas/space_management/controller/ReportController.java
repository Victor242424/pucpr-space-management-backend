package dev.victor_rivas.space_management.controller;

import dev.victor_rivas.space_management.model.dto.ApiResponse;
import dev.victor_rivas.space_management.model.dto.OccupancyReportDTO;
import dev.victor_rivas.space_management.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Endpoints for generating reports and occupancy statistics")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private final ReportService reportService;

    @Operation(
            summary = "Get occupancy report for all spaces",
            description = "Generates a complete occupancy report for all spaces in the system. " +
                    "Includes current occupancy, occupancy rate, accesses today/week/month and average duration."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Report generated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OccupancyReportDTO.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthenticated",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/occupancy")
    public ResponseEntity<ApiResponse<List<OccupancyReportDTO>>> getOccupancyReport() {
        List<OccupancyReportDTO> report = reportService.getOccupancyReport();
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @Operation(
            summary = "Get occupancy report by space",
            description = "Generates a detailed occupancy report for a specific space. " +
                    "Includes:\n" +
                    "- Current occupancy and maximum capacity\n" +
                    "- Occupancy rate in percentage\n" +
                    "- Total accesses today, this week and this month\n" +
                    "- Average visit duration"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Report generated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OccupancyReportDTO.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Space not found",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthenticated",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/occupancy/space/{spaceId}")
    public ResponseEntity<ApiResponse<OccupancyReportDTO>> getOccupancyReportBySpace(
            @Parameter(description = "Space ID", required = true, example = "1")
            @PathVariable Long spaceId) {
        OccupancyReportDTO report = reportService.getOccupancyReportBySpace(spaceId);
        return ResponseEntity.ok(ApiResponse.success(report));
    }
}