package dev.victor_rivas.space_management.controller;

import dev.victor_rivas.space_management.model.dto.ApiResponse;
import dev.victor_rivas.space_management.model.dto.StudentDTO;
import dev.victor_rivas.space_management.service.StudentService;
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
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Students", description = "Endpoints for student management")
@SecurityRequirement(name = "bearerAuth")
public class StudentController {

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    private final StudentService studentService;

    @Operation(
            summary = "Get all students",
            description = "Returns the complete list of registered students. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Student list retrieved successfully",
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
    public ResponseEntity<ApiResponse<List<StudentDTO>>> getAllStudents() {
        logger.info("Request to get all students");

        try {
            List<StudentDTO> students = studentService.getAllStudents();

            logger.info("Retrieved {} students successfully", students.size());
            return ResponseEntity.ok(ApiResponse.success(students));

        } catch (Exception e) {
            logger.error("Error retrieving all students: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Operation(
            summary = "Get student by ID",
            description = "Returns detailed information of a specific student"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Student found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StudentDTO.class)
                    )
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
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentDTO>> getStudentById(
            @Parameter(description = "Student ID", required = true)
            @PathVariable Long id) {

        logger.info("Request to get student with ID: {}", id);

        try {
            StudentDTO student = studentService.getStudentById(id);

            logger.info("Student retrieved successfully: {} - {}", id, student.getName());
            return ResponseEntity.ok(ApiResponse.success(student));

        } catch (Exception e) {
            logger.error("Error retrieving student with ID: {}. Error: {}", id, e.getMessage());
            throw e;
        }
    }

    @Operation(
            summary = "Update student",
            description = "Updates the information of an existing student"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Student updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StudentDTO.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Student not found",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid data",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthenticated",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentDTO>> updateStudent(
            @Parameter(description = "Student ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody StudentDTO studentDTO) {

        logger.info("Request to update student with ID: {}", id);
        logger.debug("Update data: name={}, email={}", studentDTO.getName(), studentDTO.getEmail());

        try {
            StudentDTO updated = studentService.updateStudent(id, studentDTO);

            logger.info("Student updated successfully: {}", id);
            return ResponseEntity.ok(ApiResponse.success("Student updated successfully", updated));

        } catch (Exception e) {
            logger.error("Error updating student with ID: {}. Error: {}", id, e.getMessage());
            throw e;
        }
    }

    @Operation(
            summary = "Delete student",
            description = "Deletes a student from the system. Requires ADMIN role. " +
                    "If the student has access records, it will be marked as INACTIVE instead of being deleted."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Student deleted successfully",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Student not found",
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
    public ResponseEntity<ApiResponse<Void>> deleteStudent(
            @Parameter(description = "Student ID", required = true)
            @PathVariable Long id) {

        logger.info("Request to delete student with ID: {}", id);

        try {
            studentService.deleteStudent(id);

            logger.info("Student deleted successfully: {}", id);
            return ResponseEntity.ok(ApiResponse.success("Student deleted successfully", null));

        } catch (Exception e) {
            logger.error("Error deleting student with ID: {}. Error: {}", id, e.getMessage());
            throw e;
        }
    }
}