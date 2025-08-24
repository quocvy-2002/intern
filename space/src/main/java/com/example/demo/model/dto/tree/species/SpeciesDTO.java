package com.example.demo.model.dto.tree.species;

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
public class SpeciesDTO {
    UUID speciesId;
    String scientificName;
    String localName;
    BigDecimal woodDensity;
    BigDecimal coeffB0;
    BigDecimal coeffB1;
    BigDecimal coeffB2;
    LocalDateTime createdAt;
}