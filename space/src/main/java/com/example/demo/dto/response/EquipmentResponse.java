package com.example.demo.dto.response;

import com.example.demo.entity.Space;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EquipmentResponse {
    Integer equipmentId;
    String equipmentName;
    Space space;
}
