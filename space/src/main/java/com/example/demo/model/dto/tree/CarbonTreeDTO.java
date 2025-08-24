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
public class CarbonTreeDTO {
    UUID recordId;
    UUID treeId;
    BigDecimal carbonKg;
    BigDecimal co2Kg;
    LocalDateTime calculatedAt;
}