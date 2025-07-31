package com.example.demo.model.dto.status;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatusRequest {
    @NotBlank(message = "EQUIPMENT_STATUS_IS_REQUIRED")
    String statusName;

    @NotBlank(message = "EQUIPMENT_STATUS_IS_REQUIRED")
    Integer equipmentTypeId;

    @NotBlank(message = "EQUIPMENT_STATUS_IS_REQUIRED")
    Integer equipmentId;
}
