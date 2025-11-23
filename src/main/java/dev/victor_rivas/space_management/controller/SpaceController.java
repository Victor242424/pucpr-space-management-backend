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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/spaces")
@RequiredArgsConstructor
@Tag(name = "Spaces", description = "Endpoints for space management (classrooms, laboratories, study rooms)")
@SecurityRequirement(name = "bearerAuth")
public class SpaceController {

    private final SpaceService spaceService;

    @Operation(
            summary = "Get all spaces",
            description = "Returns the complete list of spaces available in the system with their " +
                    "current capacity and occupancy information"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Space list retrieved successfully",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthenticated",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<SpaceDTO>>> getAllSpaces() {
        List<SpaceDTO> spaces = spaceService.getAllSpaces();
        return ResponseEntity.ok(ApiResponse.success(spaces));
    }

    @Operation(
            summary = "Get space by ID",
            description = "Returns detailed information of a specific space including its current occupancy"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Space found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SpaceDTO.class)
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
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SpaceDTO>> getSpaceById(
            @Parameter(description = "Space ID", required = true)
            @PathVariable Long id) {
        SpaceDTO space = spaceService.getSpaceById(id);
        return ResponseEntity.ok(ApiResponse.success(space));
    }

    @Operation(
            summary = "Create new space",
            description = "Creates a new space in the system. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Space created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SpaceDTO.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid data or space code already exists",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied - ADMIN role required",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthenticated",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SpaceDTO>> createSpace(
            @Valid @RequestBody SpaceDTO spaceDTO) {
        SpaceDTO created = spaceService.createSpace(spaceDTO);
        return ResponseEntity.ok(ApiResponse.success("Space created successfully", created));
    }

    @Operation(
            summary = "Update space",
            description = "Updates the information of an existing space. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Space updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SpaceDTO.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Space not found",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid data",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied - ADMIN role required",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthenticated",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SpaceDTO>> updateSpace(
            @Parameter(description = "Space ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody SpaceDTO spaceDTO) {
        SpaceDTO updated = spaceService.updateSpace(id, spaceDTO);
        return ResponseEntity.ok(ApiResponse.success("Space updated successfully", updated));
    }

    @Operation(
            summary = "Delete space",
            description = "Deletes a space from the system. Requires ADMIN role. " +
                    "If the space has access records, it will be marked as UNAVAILABLE instead of being deleted."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Space deleted successfully",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Space not found",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied - ADMIN role required",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthenticated",
                    content = @Content(mediaType = "application/json")
            )
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSpace(
            @Parameter(description = "Space ID", required = true)
            @PathVariable Long id) {
        spaceService.deleteSpace(id);
        return ResponseEntity.ok(ApiResponse.success("Space deleted successfully", null));
    }
}