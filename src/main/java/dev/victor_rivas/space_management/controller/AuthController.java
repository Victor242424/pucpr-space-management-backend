package dev.victor_rivas.space_management.controller;

import dev.victor_rivas.space_management.model.dto.*;
import dev.victor_rivas.space_management.service.AuthService;
import dev.victor_rivas.space_management.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para autenticação e registro de usuários")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final StudentService studentService;

    @Operation(
            summary = "Login",
            description = "Autenticar usuário com credenciais e obter token JWT"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login realizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credenciais inválidas",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados de entrada inválidos",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/login")
    public ResponseEntity<dev.victor_rivas.space_management.model.dto.ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        logger.info("Login attempt for username: {}", request.getUsername());

        try {
            AuthResponse response = authService.login(request);

            logger.info("Login successful for username: {}", response.getUsername());

            return ResponseEntity.ok(
                    dev.victor_rivas.space_management.model.dto.ApiResponse.success(
                            "Login successful", response));

        } catch (Exception e) {
            logger.error("Login failed for username: {}. Error: {}",
                    request.getUsername(),
                    e.getMessage());
            throw e;
        }
    }

    @Operation(
            summary = "Registrar novo estudante",
            description = "Criar uma nova conta de estudante no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estudante registrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StudentDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos ou estudante já existe",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/register")
    public ResponseEntity<dev.victor_rivas.space_management.model.dto.ApiResponse<StudentDTO>> register(
            @Valid @RequestBody CreateStudentRequest request) {

        logger.info("Registration attempt for student: {}", request.getRegistrationNumber());

        try {
            StudentDTO student = studentService.createStudent(request);

            logger.info("Student registered successfully: {} (ID: {})",
                    student.getRegistrationNumber(),
                    student.getId());

            return ResponseEntity.ok(
                    dev.victor_rivas.space_management.model.dto.ApiResponse.success(
                            "Student registered successfully", student));

        } catch (Exception e) {
            logger.error("Registration failed for student: {}. Error: {}",
                    request.getRegistrationNumber(),
                    e.getMessage());
            throw e;
        }
    }
}