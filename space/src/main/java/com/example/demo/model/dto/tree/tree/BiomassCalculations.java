package com.example.demo.model.dto.tree.tree;

import java.math.BigDecimal;

public record BiomassCalculations(
        BigDecimal biomass,
        BigDecimal carbon,
        BigDecimal co2,
        BigDecimal o2
) {}