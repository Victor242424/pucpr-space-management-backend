package dev.victor_rivas.space_management.model.dto;

import dev.victor_rivas.space_management.enums.StudentStatus;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDTO {
    private Long id;

    @NotBlank(message = "Número de matrícula é obrigatório")
    @Size(max = 20, message = "Número de matrícula não deve exceder 20 caracteres")
    private String registrationNumber;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome não deve exceder 100 caracteres")
    private String name;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Size(max = 100, message = "Email não deve exceder 100 caracteres")
    private String email;

    @Size(max = 20, message = "Número de telefone não deve exceder 20 caracteres")
    private String phoneNumber;

    private StudentStatus status;

    private String createdAt;
    private String updatedAt;
}