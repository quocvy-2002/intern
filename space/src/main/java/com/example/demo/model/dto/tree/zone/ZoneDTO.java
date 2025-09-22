package com.example.demo.model.dto.tree.zone;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ZoneDTO {
    Long zoneId;
    String zoneName;
    String zoneType;
    String boundaryWkt;
    BigDecimal area;
    BigDecimal nonGreenArea;
    Boolean isActive;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}