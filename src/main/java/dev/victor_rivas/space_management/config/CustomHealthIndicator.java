package dev.victor_rivas.space_management.config;

import dev.victor_rivas.space_management.repository.StudentRepository;
import dev.victor_rivas.space_management.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("customHealth")
@RequiredArgsConstructor
public class CustomHealthIndicator implements HealthIndicator {

    private final StudentRepository studentRepository;
    private final SpaceRepository spaceRepository;

    @Override
    public Health health() {
        try {
            long studentCount = studentRepository.count();
            long spaceCount = spaceRepository.count();

            if (studentCount >= 0 && spaceCount >= 0) {
                return Health.up()
                        .withDetail("students", studentCount)
                        .withDetail("spaces", spaceCount)
                        .withDetail("database", "Connected")
                        .build();
            } else {
                return Health.down()
                        .withDetail("error", "Invalid count")
                        .build();
            }
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
