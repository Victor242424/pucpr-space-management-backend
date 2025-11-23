package dev.victor_rivas.space_management.controller;

import dev.victor_rivas.space_management.model.dto.ApiResponse;
import dev.victor_rivas.space_management.model.dto.SpaceDTO;
import dev.victor_rivas.space_management.service.SpaceService;
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
@RequestMapping("/api/spaces")
@RequiredArgsConstructor
@Tag(name = "Espaços", description = "Endpoints para gerenciamento de espaços (salas de aula, laboratórios, salas de estudo)")
@SecurityRequirement(name = "bearerAuth")
public class SpaceController {

    private static final Logger logger = LoggerFactory.getLogger(SpaceController.class);

    private final SpaceService spaceService;

    @Operation(
            summary = "Obter todos os espaços",
            description = "Retorna a lista completa de espaços disponíveis no sistema com suas " +
                    "informações de capacidade e ocupação atuais"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Lista de espaços obtida com sucesso",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Não autenticado",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<SpaceDTO>>> getAllSpaces() {
        logger.info("Request to get all spaces");

        try {
            List<SpaceDTO> spaces = spaceService.getAllSpaces();

            logger.info("Retrieved {} spaces successfully", spaces.size());
            return ResponseEntity.ok(ApiResponse.success(spaces));

        } catch (Exception e) {
            logger.error("Error retrieving all spaces: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Operation(
            summary = "Obter espaço por ID",
            description = "Retorna informações detalhadas de um espaço específico incluindo sua ocupação atual"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Espaço encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SpaceDTO.class)
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
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SpaceDTO>> getSpaceById(
            @Parameter(description = "ID do Espaço", required = true)
            @PathVariable Long id) {

        logger.info("Request to get space with ID: {}", id);

        try {
            SpaceDTO space = spaceService.getSpaceById(id);

            logger.info("Space retrieved successfully: {} - {} (Occupancy: {}/{})",
                    id, space.getName(), space.getCurrentOccupancy(), space.getCapacity());
            return ResponseEntity.ok(ApiResponse.success(space));

        } catch (Exception e) {
            logger.error("Error retrieving space with ID: {}. Error: {}", id, e.getMessage());
            throw e;
        }
    }

    @Operation(
            summary = "Criar novo espaço",
            description = "Cria um novo espaço no sistema. Requer papel ADMIN."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Espaço criado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SpaceDTO.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos ou código do espaço já existe",
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
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SpaceDTO>> createSpace(
            @Valid @RequestBody SpaceDTO spaceDTO) {

        logger.info("Request to create new space: {} - {}", spaceDTO.getCode(), spaceDTO.getName());
        logger.debug("Space details: type={}, capacity={}, building={}",
                spaceDTO.getType(), spaceDTO.getCapacity(), spaceDTO.getBuilding());

        try {
            SpaceDTO created = spaceService.createSpace(spaceDTO);

            logger.info("Space created successfully: {} (ID: {})", created.getCode(), created.getId());
            return ResponseEntity.ok(ApiResponse.success("Space created successfully", created));

        } catch (Exception e) {
            logger.error("Error creating space: {}. Error: {}", spaceDTO.getCode(), e.getMessage());
            throw e;
        }
    }

    @Operation(
            summary = "Atualizar espaço",
            description = "Atualiza as informações de um espaço existente. Requer papel ADMIN."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Espaço atualizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SpaceDTO.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Espaço não encontrado",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos",
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
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SpaceDTO>> updateSpace(
            @Parameter(description = "ID do Espaço", required = true)
            @PathVariable Long id,
            @Valid @RequestBody SpaceDTO spaceDTO) {

        logger.info("Request to update space with ID: {}", id);
        logger.debug("Update data: code={}, name={}, capacity={}",
                spaceDTO.getCode(), spaceDTO.getName(), spaceDTO.getCapacity());

        try {
            SpaceDTO updated = spaceService.updateSpace(id, spaceDTO);

            logger.info("Space updated successfully: {}", id);
            return ResponseEntity.ok(ApiResponse.success("Space updated successfully", updated));

        } catch (Exception e) {
            logger.error("Error updating space with ID: {}. Error: {}", id, e.getMessage());
            throw e;
        }
    }

    @Operation(
            summary = "Excluir espaço",
            description = "Exclui um espaço do sistema. Requer papel ADMIN. " +
                    "Se o espaço tiver registros de acesso, será marcado como INDISPONÍVEL em vez de ser excluído."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Espaço excluído com sucesso",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Espaço não encontrado",
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
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSpace(
            @Parameter(description = "ID do Espaço", required = true)
            @PathVariable Long id) {

        logger.info("Request to delete space with ID: {}", id);

        try {
            spaceService.deleteSpace(id);

            logger.info("Space deleted successfully: {}", id);
            return ResponseEntity.ok(ApiResponse.success("Space deleted successfully", null));

        } catch (Exception e) {
            logger.error("Error deleting space with ID: {}. Error: {}", id, e.getMessage());
            throw e;
        }
    }
}
