package com.example.demo.model.dto.weather.alert;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AlertTypeCreateDTO {
    @NotBlank(message = "IS_REQUIRED")
    String typeCode;

    String description;

    BigDecimal thresholdValue;

    String thresholdField;
}