package com.example.demo.model.dto.equipmenttype;

import com.example.demo.model.entity.EquipmentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ETypeDTO {
    Integer equipmentTypeId;
    String equipmentTypeName;
    List<EquipmentStatus> equipmentStatus;
}
