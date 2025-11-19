package dev.victor_rivas.space_management.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExitRequest {

    @NotNull(message = "Access record ID is required")
    private Long accessRecordId;

    private String notes;
}