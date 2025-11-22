package dev.victor_rivas.space_management.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    /**
     * Personaliza el registro de métricas con tags comunes
     */
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config()
                .commonTags("application", "space-management");
    }

    /**
     * Habilita la anotación @Timed para métodos
     */
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    /**
     * Métricas personalizadas para el negocio
     */
    @Bean
    public MeterBinder customMetrics(MeterRegistry registry) {
        return meterRegistry -> {
            // Aquí puedes agregar métricas personalizadas si lo deseas
            // Por ejemplo, contadores, gauges, etc.
        };
    }
}
