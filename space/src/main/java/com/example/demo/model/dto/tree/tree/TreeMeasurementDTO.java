package com.example.demo.model.dto.tree.tree;

import com.example.demo.model.enums.HealthStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreeMeasurementDTO {
    private TreeCreateDTO tree;
    private BigDecimal dbhCm;
    private BigDecimal heightM;
    private BigDecimal canopyDiameterM;
    private HealthStatus healthStatus;
    private LocalDateTime measuredAt;
}