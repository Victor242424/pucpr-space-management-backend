package dev.victor_rivas.space_management.service;

import dev.victor_rivas.space_management.exception.ResourceNotFoundException;
import dev.victor_rivas.space_management.logging.LoggingUtils;
import dev.victor_rivas.space_management.model.dto.AuthResponse;
import dev.victor_rivas.space_management.model.dto.LoginRequest;
import dev.victor_rivas.space_management.model.entity.User;
import dev.victor_rivas.space_management.repository.UserRepository;
import dev.victor_rivas.space_management.security.JwtTokenProvider;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final MetricsService metricsService;

    @Timed(value = "auth.login", description = "Time to authenticate a user")
    public AuthResponse login(LoginRequest request) {
        String username = request.getUsername();

        log.info("Attempting authentication for user: {}", username);
        log.debug("Login request received | username={}", username);

        try {
            // Autenticación
            log.debug("Authenticating user credentials | username={}", username);
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // Establecer contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Security context established | username={}", username);

            // Generar token
            log.debug("Generating JWT token | username={}", username);
            String token = tokenProvider.generateToken(authentication);

            // Obtener información del usuario
            log.debug("Fetching user details from database | username={}", username);
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> {
                        log.error("User not found after successful authentication | username={}", username);
                        return new ResourceNotFoundException("User not found");
                    });

            // Construir respuesta
            AuthResponse response = AuthResponse.builder()
                    .token(token)
                    .type("Bearer")
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .studentId(user.getStudent() != null ? user.getStudent().getId() : null)
                    .build();

            // Registrar métricas y logs de éxito
            metricsService.recordLoginSuccess();

            log.info("Authentication successful | username={} | role={} | studentId={}",
                    user.getUsername(),
                    user.getRole().name(),
                    response.getStudentId());

            // Log estructurado de auditoría
            LoggingUtils.logAuthEvent(
                    "LOGIN_SUCCESS",
                    username,
                    true,
                    String.format("role=%s, studentId=%s", user.getRole().name(), response.getStudentId())
            );

            // Establecer contexto del usuario para logs posteriores
            LoggingUtils.setUserContext(user.getId(), user.getUsername());

            return response;

        } catch (BadCredentialsException e) {
            // Registrar métricas y logs de fallo
            metricsService.recordLoginFailure();

            log.warn("Authentication failed - Invalid credentials | username={}", username);
            log.debug("BadCredentialsException details", e);

            // Log estructurado de auditoría
            LoggingUtils.logAuthEvent(
                    "LOGIN_FAILED",
                    username,
                    false,
                    "Invalid credentials"
            );

            throw e;

        } catch (Exception e) {
            log.error("Unexpected error during authentication | username={} | error={}",
                    username,
                    e.getMessage());
            log.debug("Exception details", e);

            // Log estructurado de error
            LoggingUtils.logError(
                    "AuthService.login",
                    e.getClass().getSimpleName(),
                    e.getMessage(),
                    e
            );

            // Log estructurado de auditoría
            LoggingUtils.logAuthEvent(
                    "LOGIN_ERROR",
                    username,
                    false,
                    String.format("Error: %s", e.getMessage())
            );

            throw e;
        }
    }
}
