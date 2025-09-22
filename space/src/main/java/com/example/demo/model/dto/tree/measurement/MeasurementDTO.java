package com.example.demo.model.dto.tree.measurement;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MeasurementDTO {
    Long measurementId;
    Long treeId;
    BigDecimal girthCm;
    BigDecimal heightM;
    BigDecimal canopyDiameterM;
    BigDecimal leafAreaM2;
    String healthStatus;
    LocalDateTime measuredAt;
    LocalDateTime createdAt;
    String code;
    String descriptionStatus;

    BigDecimal biomassKg;
    BigDecimal carbonKg;
    BigDecimal co2AbsorbedKg;
    BigDecimal o2ReleasedKg;

    public MeasurementDTO(Long measurementId, LocalDateTime measuredAt, Long treeId, String code) {
        this.measurementId = measurementId;
        this.measuredAt = measuredAt;
        this.treeId = treeId;
        this.code = code;
    }
}