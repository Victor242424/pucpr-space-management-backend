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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/access")
@RequiredArgsConstructor
@Tag(name = "Access Records", description = "Endpoints for space access record management")
@SecurityRequirement(name = "bearerAuth")
public class AccessRecordController {

    private final AccessRecordService accessRecordService;

    @Operation(
            summary = "Register entry to a space",
            description = "Registers a student's entry to a specific space. " +
                    "Validates that the student is active, the space is available and does not exceed maximum capacity."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Entry registered successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccessRecordDTO.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Validation error: inactive student, space not available, " +
                            "maximum capacity reached or student already has an active access",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Student or space not found",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthenticated",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/entry")
    public ResponseEntity<ApiResponse<AccessRecordDTO>> registerEntry(
            @Valid @RequestBody EntryRequest request) {
        AccessRecordDTO accessRecord = accessRecordService.registerEntry(request);
        return ResponseEntity.ok(ApiResponse.success("Entry registered successfully", accessRecord));
    }

    @Operation(
            summary = "Register exit from a space",
            description = "Registers a student's exit from a space and calculates the visit duration"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Exit registered successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccessRecordDTO.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Access record is not active",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Access record not found",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthenticated",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/exit")
    public ResponseEntity<ApiResponse<AccessRecordDTO>> registerExit(
            @Valid @RequestBody ExitRequest request) {
        AccessRecordDTO accessRecord = accessRecordService.registerExit(request);
        return ResponseEntity.ok(ApiResponse.success("Exit registered successfully", accessRecord));
    }

    @Operation(
            summary = "Get all access records",
            description = "Returns the complete list of access records. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Access records list retrieved successfully",
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
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AccessRecordDTO>>> getAllAccessRecords() {
        List<AccessRecordDTO> records = accessRecordService.getAllAccessRecords();
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @Operation(
            summary = "Get access records by student",
            description = "Returns all access records for a specific student"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Records retrieved successfully",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Student not found",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthenticated",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<AccessRecordDTO>>> getAccessRecordsByStudent(
            @Parameter(description = "Student ID", required = true)
            @PathVariable Long studentId) {
        List<AccessRecordDTO> records = accessRecordService.getAccessRecordsByStudent(studentId);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @Operation(
            summary = "Get access records by space",
            description = "Returns all access records for a specific space"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Records retrieved successfully",
                    content = @Content(mediaType = "application/json")
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
    @GetMapping("/space/{spaceId}")
    public ResponseEntity<ApiResponse<List<AccessRecordDTO>>> getAccessRecordsBySpace(
            @Parameter(description = "Space ID", required = true)
            @PathVariable Long spaceId) {
        List<AccessRecordDTO> records = accessRecordService.getAccessRecordsBySpace(spaceId);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @Operation(
            summary = "Get active access records",
            description = "Returns all access records that are currently in progress " +
                    "(students who have entered but have not yet exited)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Active records retrieved successfully",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthenticated",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<AccessRecordDTO>>> getActiveAccessRecords() {
        List<AccessRecordDTO> records = accessRecordService.getActiveAccessRecords();
        return ResponseEntity.ok(ApiResponse.success(records));
    }
}