package dev.victor_rivas.space_management.model.dto;

import dev.victor_rivas.space_management.enums.SpaceStatus;
import dev.victor_rivas.space_management.enums.SpaceType;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpaceDTO {
    private Long id;

    @NotBlank(message = "Code is required")
    @Size(max = 50, message = "Code must not exceed 50 characters")
    private String code;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotNull(message = "Type is required")
    private SpaceType type;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    private String building;
    private String floor;
    private String description;
    private SpaceStatus status;
    private Integer currentOccupancy;
    private String createdAt;
    private String updatedAt;
}