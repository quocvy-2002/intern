package com.example.demo.model.dto.tree;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GreenDTO {
    BigDecimal area;
    Integer treeCount;
    BigDecimal gnprGross;
    BigDecimal gnprNet;
}
