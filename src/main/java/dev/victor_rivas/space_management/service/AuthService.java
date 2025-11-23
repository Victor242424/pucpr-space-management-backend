package dev.victor_rivas.space_management.service;

import dev.victor_rivas.space_management.exception.ResourceNotFoundException;
import dev.victor_rivas.space_management.model.dto.AuthResponse;
import dev.victor_rivas.space_management.model.dto.LoginRequest;
import dev.victor_rivas.space_management.model.entity.User;
import dev.victor_rivas.space_management.repository.UserRepository;
import dev.victor_rivas.space_management.security.JwtTokenProvider;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final MetricsService metricsService;

    @Timed(value = "auth.login", description = "Time to authenticate a user")
    public AuthResponse login(LoginRequest request) {
        logger.info("Attempting login for username: {}", request.getUsername());

        try {
            // Autenticar usuario
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.debug("Authentication successful for username: {}", request.getUsername());

            // Generar token JWT
            String token = tokenProvider.generateToken(authentication);
            logger.debug("JWT token generated for username: {}", request.getUsername());

            // Obtener información del usuario
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

            // Registrar métrica de éxito
            metricsService.recordLoginSuccess();

            // Construir respuesta
            AuthResponse response = AuthResponse.builder()
                    .token(token)
                    .type("Bearer")
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .studentId(user.getStudent() != null ? user.getStudent().getId() : null)
                    .build();

            logger.info("Login successful for username: {} with role: {}",
                    user.getUsername(),
                    user.getRole().name());

            return response;

        } catch (BadCredentialsException e) {
            metricsService.recordLoginFailure();
            logger.warn("Login failed for username: {} - Invalid credentials", request.getUsername());
            throw e;

        } catch (Exception e) {
            logger.error("Unexpected error during login for username: {}",
                    request.getUsername(), e);
            throw e;
        }
    }
}