package com.example.demo.model.dto.tree.species;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpeciesCreateDTO {
    @NotBlank(message = "IS_REQUIRED")
    String scientificName;

    @NotBlank(message = "IS_REQUIRED")
    String localName;

    @NotNull(message = "IS_REQUIRED")
    BigDecimal woodDensity;

    @NotNull(message = "IS_REQUIRED")
    BigDecimal coeffB0;

    @NotNull(message = "IS_REQUIRED")
    BigDecimal coeffB1;

    @NotNull(message = "IS_REQUIRED")
    BigDecimal coeffB2;
}
