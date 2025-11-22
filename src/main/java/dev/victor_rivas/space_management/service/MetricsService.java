package dev.victor_rivas.space_management.service;

import dev.victor_rivas.space_management.enums.AccessStatus;
import dev.victor_rivas.space_management.enums.SpaceStatus;
import dev.victor_rivas.space_management.repository.AccessRecordRepository;
import dev.victor_rivas.space_management.repository.SpaceRepository;
import dev.victor_rivas.space_management.repository.StudentRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class MetricsService {

    private final MeterRegistry meterRegistry;
    private final AccessRecordRepository accessRecordRepository;
    private final SpaceRepository spaceRepository;
    private final StudentRepository studentRepository;

    // Contadores
    private final Counter entryCounter;
    private final Counter exitCounter;
    private final Counter loginSuccessCounter;
    private final Counter loginFailureCounter;

    // Timers
    private final Timer entryRegistrationTimer;
    private final Timer exitRegistrationTimer;

    public MetricsService(MeterRegistry meterRegistry,
                          AccessRecordRepository accessRecordRepository,
                          SpaceRepository spaceRepository,
                          StudentRepository studentRepository) {
        this.meterRegistry = meterRegistry;
        this.accessRecordRepository = accessRecordRepository;
        this.spaceRepository = spaceRepository;
        this.studentRepository = studentRepository;

        // Inicializar contadores
        this.entryCounter = Counter.builder("space.entry.total")
                .description("Total number of space entries")
                .register(meterRegistry);

        this.exitCounter = Counter.builder("space.exit.total")
                .description("Total number of space exits")
                .register(meterRegistry);

        this.loginSuccessCounter = Counter.builder("auth.login.success")
                .description("Successful login attempts")
                .register(meterRegistry);

        this.loginFailureCounter = Counter.builder("auth.login.failure")
                .description("Failed login attempts")
                .register(meterRegistry);

        // Inicializar timers
        this.entryRegistrationTimer = Timer.builder("space.entry.registration.time")
                .description("Time taken to register an entry")
                .register(meterRegistry);

        this.exitRegistrationTimer = Timer.builder("space.exit.registration.time")
                .description("Time taken to register an exit")
                .register(meterRegistry);

        // Registrar Gauges
        registerGauges();
    }

    private void registerGauges() {
        // Total de estudiantes activos
        Gauge.builder("students.active.total", studentRepository, repo ->
                        repo.count())
                .description("Total number of active students")
                .register(meterRegistry);

        // Total de espacios
        Gauge.builder("spaces.total", spaceRepository, repo ->
                        repo.count())
                .description("Total number of spaces")
                .register(meterRegistry);

        // Espacios disponibles
        Gauge.builder("spaces.available.total", spaceRepository, repo ->
                        repo.findByStatus(SpaceStatus.AVAILABLE).size())
                .description("Number of available spaces")
                .register(meterRegistry);

        // Espacios ocupados
        Gauge.builder("spaces.occupied.total", spaceRepository, repo ->
                        repo.findByStatus(SpaceStatus.OCCUPIED).size())
                .description("Number of occupied spaces")
                .register(meterRegistry);

        // Accesos activos actuales
        Gauge.builder("access.active.current", accessRecordRepository, repo ->
                        repo.findByStatus(AccessStatus.ACTIVE).size())
                .description("Current number of active accesses")
                .register(meterRegistry);

        // Total de accesos completados
        Gauge.builder("access.completed.total", accessRecordRepository, repo ->
                        repo.findByStatus(AccessStatus.COMPLETED).size())
                .description("Total number of completed accesses")
                .register(meterRegistry);
    }

    // Métodos para incrementar contadores
    public void recordEntry() {
        entryCounter.increment();
    }

    public void recordExit() {
        exitCounter.increment();
    }

    public void recordLoginSuccess() {
        loginSuccessCounter.increment();
    }

    public void recordLoginFailure() {
        loginFailureCounter.increment();
    }

    // Métodos para obtener timers
    public Timer getEntryRegistrationTimer() {
        return entryRegistrationTimer;
    }

    public Timer getExitRegistrationTimer() {
        return exitRegistrationTimer;
    }

    // Método para registrar métricas personalizadas por evento
    public void recordCustomMetric(String metricName, String description, double value) {
        meterRegistry.counter(metricName, "description", description).increment(value);
    }
}
