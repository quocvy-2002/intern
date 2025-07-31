package com.example.demo.model.dto.equipment;

import com.example.demo.model.entity.Space;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EquipmentDTO {
    Integer equipmentId;
    String equipmentName;
    Space space;
}
