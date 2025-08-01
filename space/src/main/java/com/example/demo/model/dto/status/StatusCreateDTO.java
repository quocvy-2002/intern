package com.example.demo.model.dto.status;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatusCreateDTO {
    @NotBlank(message = "IS_REQUIRED")
    String statusName;

    @NotNull(message = "IS_REQUIRED")
    Integer equipmentTypeId;
}
