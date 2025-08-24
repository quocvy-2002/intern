package com.example.demo.model.dto.tree.zone;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ZoneDTO {
    UUID zoneId;
    String zoneName;
    String zoneType;
    String boundaryWkt;
    Boolean isActive;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}