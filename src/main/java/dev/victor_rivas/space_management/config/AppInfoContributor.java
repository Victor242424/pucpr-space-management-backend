package dev.victor_rivas.space_management.config;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class AppInfoContributor implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        Map<String, Object> details = new HashMap<>();
        details.put("startup-time", LocalDateTime.now().toString());
        details.put("api-version", "v1");
        details.put("description", "Space Management System API");
        details.put("contact", "support@spacemanagement.com");

        builder.withDetail("custom-info", details);
    }
}
