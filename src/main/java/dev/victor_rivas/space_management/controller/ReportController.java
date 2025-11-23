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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Relatórios", description = "Endpoints para gerar relatórios e estatísticas de ocupação")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    private final ReportService reportService;

    @Operation(
            summary = "Obter relatório de ocupação de todos os espaços",
            description = "Gera um relatório completo de ocupação de todos os espaços do sistema. " +
                    "Inclui ocupação atual, taxa de ocupação, acessos hoje/semana/mês e duração média."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Relatório gerado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OccupancyReportDTO.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Não autenticado",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/occupancy")
    public ResponseEntity<ApiResponse<List<OccupancyReportDTO>>> getOccupancyReport() {
        logger.info("Request to generate occupancy report for all spaces");

        try {
            List<OccupancyReportDTO> report = reportService.getOccupancyReport();

            logger.info("Occupancy report generated successfully for {} spaces", report.size());

            // Log detalle de ocupación
            report.forEach(r -> logger.debug("Space: {} - Occupancy: {}/{} ({}%)",
                    r.getSpaceName(),
                    r.getCurrentOccupancy(),
                    r.getCapacity(),
                    r.getOccupancyRate()));

            return ResponseEntity.ok(ApiResponse.success(report));

        } catch (Exception e) {
            logger.error("Error generating occupancy report: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Operation(
            summary = "Obter relatório de ocupação por espaço",
            description = "Gera um relatório detalhado de ocupação de um espaço específico. " +
                    "Inclui:\n" +
                    "- Ocupação atual e capacidade máxima\n" +
                    "- Taxa de ocupação em percentual\n" +
                    "- Total de acessos hoje, nesta semana e neste mês\n" +
                    "- Duração média das visitas"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Relatório gerado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OccupancyReportDTO.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Espaço não encontrado",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Não autenticado",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/occupancy/space/{spaceId}")
    public ResponseEntity<ApiResponse<OccupancyReportDTO>> getOccupancyReportBySpace(
            @Parameter(description = "ID do Espaço", required = true, example = "1")
            @PathVariable Long spaceId) {

        logger.info("Request to generate occupancy report for space ID: {}", spaceId);

        try {
            OccupancyReportDTO report = reportService.getOccupancyReportBySpace(spaceId);

            logger.info("Occupancy report generated for space ID: {} - {} (Occupancy: {}%, Avg Duration: {} min)",
                    spaceId,
                    report.getSpaceName(),
                    report.getOccupancyRate(),
                    report.getAverageDurationInMinutes());

            logger.debug("Report details - Today: {}, Week: {}, Month: {}",
                    report.getTotalAccessesToday(),
                    report.getTotalAccessesThisWeek(),
                    report.getTotalAccessesThisMonth());

            return ResponseEntity.ok(ApiResponse.success(report));

        } catch (Exception e) {
            logger.error("Error generating occupancy report for space ID: {}. Error: {}",
                    spaceId, e.getMessage());
            throw e;
        }
    }
}