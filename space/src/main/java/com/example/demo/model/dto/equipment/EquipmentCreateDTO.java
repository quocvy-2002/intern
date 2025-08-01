package com.example.demo.model.dto.equipment;

import com.example.demo.model.entity.Space;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EquipmentCreateDTO {
    @NotBlank(message = "IS_REQUIRED")
    String equipmentName;

    @NotNull(message = "IS_REQUIRED")
    Space space;

    @NotNull(message = "IS_REQUIRED")
    Integer equipmentTypeId;
}
