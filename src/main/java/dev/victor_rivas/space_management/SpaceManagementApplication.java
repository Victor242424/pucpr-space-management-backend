package dev.victor_rivas.space_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class SpaceManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpaceManagementApplication.class, args);
    }

}