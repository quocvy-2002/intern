package com.example.demo.model.dto.tree;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CarbonSummary {
    BigDecimal biomass;
    BigDecimal carbon;
    BigDecimal co2;
    BigDecimal o2;
    BigDecimal leafArea;
    BigDecimal waterLoss;
}
