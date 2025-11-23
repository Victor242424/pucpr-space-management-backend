package dev.victor_rivas.space_management.controller;

import dev.victor_rivas.space_management.model.dto.AccessRecordDTO;
import dev.victor_rivas.space_management.model.dto.ApiResponse;
import dev.victor_rivas.space_management.model.dto.EntryRequest;
import dev.victor_rivas.space_management.model.dto.ExitRequest;
import dev.victor_rivas.space_management.service.AccessRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/access")
@RequiredArgsConstructor
@Tag(name = "Registros de Acesso", description = "Endpoints para gerenciamento de registros de acesso aos espaços")
@SecurityRequirement(name = "bearerAuth")
public class AccessRecordController {

    private static final Logger logger = LoggerFactory.getLogger(AccessRecordController.class);

    private final AccessRecordService accessRecordService;

    @Operation(
            summary = "Registrar entrada em um espaço",
            description = "Registra a entrada de um estudante em um espaço específico. " +
                    "Valida que o estudante esteja ativo, o espaço esteja disponível e não exceda a capacidade máxima."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Entrada registrada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccessRecordDTO.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Erro de validação: estudante inativo, espaço não disponível, " +
                            "capacidade máxima atingida ou estudante já possui acesso ativo",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Estudante ou espaço não encontrado",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Não autenticado",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/entry")
    public ResponseEntity<ApiResponse<AccessRecordDTO>> registerEntry(
            @Valid @RequestBody EntryRequest request) {

        logger.info("Entry registration request - Student ID: {}, Space ID: {}",
                request.getStudentId(), request.getSpaceId());
        logger.debug("Entry notes: {}", request.getNotes());

        try {
            AccessRecordDTO accessRecord = accessRecordService.registerEntry(request);

            logger.info("Entry registered successfully - Record ID: {}, Student: {}, Space: {}",
                    accessRecord.getId(),
                    request.getStudentId(),
                    request.getSpaceId());

            return ResponseEntity.ok(
                    ApiResponse.success("Entry registered successfully", accessRecord));

        } catch (Exception e) {
            logger.error("Entry registration failed - Student: {}, Space: {}. Error: {}",
                    request.getStudentId(),
                    request.getSpaceId(),
                    e.getMessage());
            throw e;
        }
    }

    @Operation(
            summary = "Registrar saída de um espaço",
            description = "Registra a saída de um estudante de um espaço e calcula a duração da visita"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Saída registrada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccessRecordDTO.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Registro de acesso não está ativo",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Registro de acesso não encontrado",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Não autenticado",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/exit")
    public ResponseEntity<ApiResponse<AccessRecordDTO>> registerExit(
            @Valid @RequestBody ExitRequest request) {

        logger.info("Exit registration request - Access Record ID: {}",
                request.getAccessRecordId());
        logger.debug("Exit notes: {}", request.getNotes());

        try {
            AccessRecordDTO accessRecord = accessRecordService.registerExit(request);

            logger.info("Exit registered successfully - Record ID: {}, Duration: {} minutes",
                    accessRecord.getId(),
                    accessRecord.getDurationInMinutes());

            return ResponseEntity.ok(
                    ApiResponse.success("Exit registered successfully", accessRecord));

        } catch (Exception e) {
            logger.error("Exit registration failed - Record ID: {}. Error: {}",
                    request.getAccessRecordId(),
                    e.getMessage());
            throw e;
        }
    }

    @Operation(
            summary = "Obter todos os registros de acesso",
            description = "Retorna a lista completa de registros de acesso. Requer papel ADMIN."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Lista de registros de acesso obtida com sucesso",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Acesso negado - papel ADMIN necessário",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Não autenticado",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AccessRecordDTO>>> getAllAccessRecords() {
        logger.info("Request to get all access records");

        try {
            List<AccessRecordDTO> records = accessRecordService.getAllAccessRecords();

            logger.info("Retrieved {} access records successfully", records.size());
            return ResponseEntity.ok(ApiResponse.success(records));

        } catch (Exception e) {
            logger.error("Error retrieving all access records: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Operation(
            summary = "Obter registros de acesso por estudante",
            description = "Retorna todos os registros de acesso de um estudante específico"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Registros obtidos com sucesso",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Estudante não encontrado",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Não autenticado",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<AccessRecordDTO>>> getAccessRecordsByStudent(
            @Parameter(description = "ID do Estudante", required = true)
            @PathVariable Long studentId) {

        logger.info("Request to get access records for student ID: {}", studentId);

        try {
            List<AccessRecordDTO> records = accessRecordService.getAccessRecordsByStudent(studentId);

            logger.info("Retrieved {} access records for student ID: {}", records.size(), studentId);
            return ResponseEntity.ok(ApiResponse.success(records));

        } catch (Exception e) {
            logger.error("Error retrieving access records for student ID: {}. Error: {}",
                    studentId, e.getMessage());
            throw e;
        }
    }

    @Operation(
            summary = "Obter registros de acesso por espaço",
            description = "Retorna todos os registros de acesso de um espaço específico"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Registros obtidos com sucesso",
                    content = @Content(mediaType = "application/json")
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
    @GetMapping("/space/{spaceId}")
    public ResponseEntity<ApiResponse<List<AccessRecordDTO>>> getAccessRecordsBySpace(
            @Parameter(description = "ID do Espaço", required = true)
            @PathVariable Long spaceId) {

        logger.info("Request to get access records for space ID: {}", spaceId);

        try {
            List<AccessRecordDTO> records = accessRecordService.getAccessRecordsBySpace(spaceId);

            logger.info("Retrieved {} access records for space ID: {}", records.size(), spaceId);
            return ResponseEntity.ok(ApiResponse.success(records));

        } catch (Exception e) {
            logger.error("Error retrieving access records for space ID: {}. Error: {}",
                    spaceId, e.getMessage());
            throw e;
        }
    }

    @Operation(
            summary = "Obter registros de acesso ativos",
            description = "Retorna todos os registros de acesso que estão atualmente em andamento " +
                    "(estudantes que entraram mas ainda não saíram)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Registros ativos obtidos com sucesso",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Não autenticado",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<AccessRecordDTO>>> getActiveAccessRecords() {
        logger.info("Request to get active access records");

        try {
            List<AccessRecordDTO> records = accessRecordService.getActiveAccessRecords();

            logger.info("Retrieved {} active access records", records.size());
            return ResponseEntity.ok(ApiResponse.success(records));

        } catch (Exception e) {
            logger.error("Error retrieving active access records: {}", e.getMessage(), e);
            throw e;
        }
    }
}
