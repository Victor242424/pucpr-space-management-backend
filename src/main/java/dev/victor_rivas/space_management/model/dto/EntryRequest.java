package dev.victor_rivas.space_management.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntryRequest {

    @NotNull(message = "ID do estudante é obrigatório")
    private Long studentId;

    @NotNull(message = "ID do espaço é obrigatório")
    private Long spaceId;

    private String notes;
}