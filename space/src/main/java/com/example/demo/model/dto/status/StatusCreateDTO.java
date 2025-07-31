package com.example.demo.model.dto.status;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatusCreateDTO {
    @NotBlank(message = "EQUIPMENT_STATUS_IS_REQUIRED")
    String statusName;

    @NotBlank(message = "EQUIPMENT_STATUS_IS_REQUIRED")
    Integer equipmentTypeId;
}
