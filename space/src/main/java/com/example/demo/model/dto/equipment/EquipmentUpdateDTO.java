package com.example.demo.model.dto.equipment;

import com.example.demo.model.entity.Space;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EquipmentUpdateDTO {
    @NotBlank(message = "EQUIPMENT_IS_REQUIRED")
    String equipmentName;

    @NotBlank(message = "EQUIPMENT_IS_REQUIRED")
    Space space;
}
