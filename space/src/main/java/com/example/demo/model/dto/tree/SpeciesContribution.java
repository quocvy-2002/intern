package com.example.demo.model.dto.tree;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpeciesContribution {
    String speciesName;
    BigDecimal leafArea;
    BigDecimal co2;
    BigDecimal o2;
}
