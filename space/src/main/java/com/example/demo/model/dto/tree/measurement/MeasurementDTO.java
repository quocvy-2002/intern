package com.example.demo.model.dto.tree.measurement;

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
public class MeasurementDTO {
    UUID measurementId;
    UUID treeId;
    BigDecimal dbhCm;
    BigDecimal heightM;
    BigDecimal canopyDiameterM;
    String healthStatus;
    LocalDateTime measuredAt;
    LocalDateTime createdAt;
}