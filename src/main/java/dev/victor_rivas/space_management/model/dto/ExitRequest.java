package dev.victor_rivas.space_management.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExitRequest {

    @NotNull(message = "ID do registro de acesso é obrigatório")
    private Long accessRecordId;

    private String notes;
}