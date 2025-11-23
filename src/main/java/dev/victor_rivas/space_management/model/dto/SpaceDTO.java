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

    @NotBlank(message = "Código é obrigatório")
    @Size(max = 50, message = "Código não deve exceder 50 caracteres")
    private String code;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome não deve exceder 100 caracteres")
    private String name;

    @NotNull(message = "Tipo é obrigatório")
    private SpaceType type;

    @NotNull(message = "Capacidade é obrigatória")
    @Min(value = 1, message = "Capacidade deve ser no mínimo 1")
    private Integer capacity;

    private String building;
    private String floor;
    private String description;
    private SpaceStatus status;
    private Integer currentOccupancy;
    private String createdAt;
    private String updatedAt;
}