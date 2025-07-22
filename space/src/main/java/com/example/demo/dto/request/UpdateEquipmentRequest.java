package com.example.demo.dto.request;

import com.example.demo.entity.Space;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEquipmentRequest {
    String equipmentName;
    Space space;
}
