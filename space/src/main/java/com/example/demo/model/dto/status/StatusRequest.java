package com.example.demo.model.dto.status;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatusRequest {
    @NotBlank(message = "IS_REQUIRED")
    String statusName;

    @NotNull(message = "IS_REQUIRED")
    Integer equipmentTypeId;

    @NotNull(message = "IS_REQUIRED")
    Integer equipmentId;
}
