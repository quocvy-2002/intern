package com.example.demo.model.dto.status;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatusUpdateDTO {
    @NotBlank(message = "EQUIPMENT_STATUS_IS_REQUIRED")
    String statusName;
}
