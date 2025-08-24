package com.example.demo.model.dto.tree;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CarbonSnapshotDTO {
    UUID snapshotId;
    UUID zoneId;
    BigDecimal totalCarbonKg;
    BigDecimal totalCo2Kg;
    Integer treeCount;
    LocalDateTime calculatedAt;
}