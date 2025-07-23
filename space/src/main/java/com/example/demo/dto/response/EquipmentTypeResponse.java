package com.example.demo.dto.response;

import com.example.demo.entity.EquipmentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EquipmentTypeResponse {
    Integer equipmentTypeId;
    String equipmentTypeName;
    List<EquipmentStatus> equipmentStatus;

}
