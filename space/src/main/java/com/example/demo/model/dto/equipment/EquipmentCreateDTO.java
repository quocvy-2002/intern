package com.example.demo.model.dto.equipment;

import com.example.demo.model.entity.Space;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EquipmentCreateDTO {
    @NotBlank(message = "EQUIPMENT_IS_REQUIRED")
    String equipmentName;

    @NotBlank(message = "EQUIPMENT_IS_REQUIRED")
    Space space;

    @NotBlank(message = "EQUIPMENT_IS_REQUIRED")
    Integer equipmentTypeId;
}
