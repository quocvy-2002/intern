package com.example.demo.model.dto.weather.alert;

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
public class AlertTypeUpdateDTO {
    @NotBlank(message = "IS_REQUIRED")
    String description;

    @NotNull(message = "IS_REQUIRED")
    BigDecimal thresholdValue;

    @NotNull(message = "IS_REQUIRED")
    String thresholdField;
}