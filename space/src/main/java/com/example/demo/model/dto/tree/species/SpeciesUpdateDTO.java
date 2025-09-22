package com.example.demo.model.dto.tree.species;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpeciesUpdateDTO {
    String localName;

    BigDecimal woodDensity;

    BigDecimal coeffB0;

    BigDecimal coeffB1;

    BigDecimal coeffB2;

    Double  lai;

    BigDecimal plantFactor;
}