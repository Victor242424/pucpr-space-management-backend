package dev.victor_rivas.space_management.model.dto;

import dev.victor_rivas.space_management.enums.AccessStatus;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessRecordDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentRegistrationNumber;
    private Long spaceId;
    private String spaceName;
    private String spaceCode;
    private String entryTime;
    private String exitTime;
    private Long durationInMinutes;
    private AccessStatus status;
    private String notes;
    private String createdAt;
}
