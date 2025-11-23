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
@Tag(name = "Access Records", description = "Endpoints for space access record management")
@SecurityRequirement(name = "bearerAuth")
public class AccessRecordController {

    private static final Logger logger = LoggerFactory.getLogger(AccessRecordController.class);

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
