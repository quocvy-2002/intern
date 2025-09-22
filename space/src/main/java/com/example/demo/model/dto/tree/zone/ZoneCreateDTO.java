package com.example.demo.model.dto.tree.zone;

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
public class ZoneCreateDTO {
    @NotBlank(message = "IS_REQUIRED")
    String zoneName;

    String zoneType;

    @NotNull(message = "IS_REQUIRED")
    String boundaryWkt;

    @NotNull(message = "IS_REQUIRED")
    BigDecimal area;

    @NotNull(message = "IS_REQUIRED")
    BigDecimal nonGreenArea;
}