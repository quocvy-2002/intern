package com.example.demo.model.dto.tree.species;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpeciesDTO {
    Long speciesId;
    String scientificName;
    String localName;
    BigDecimal woodDensity;
    BigDecimal coeffB0;
    BigDecimal coeffB1;
    BigDecimal coeffB2;
    Double  lai;
    LocalDateTime createdAt;
    BigDecimal plantFactor;
    Integer totalTree;
}