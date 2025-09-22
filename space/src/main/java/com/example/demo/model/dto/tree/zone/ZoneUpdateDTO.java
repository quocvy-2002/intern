package com.example.demo.model.dto.tree.zone;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ZoneUpdateDTO {
    String zoneName;

    String zoneType;

    String boundaryWkt;

    BigDecimal area;

    Boolean isActive;

    BigDecimal nonGreenArea;
}