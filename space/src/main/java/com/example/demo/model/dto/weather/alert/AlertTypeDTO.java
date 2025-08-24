package com.example.demo.model.dto.weather.alert;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AlertTypeDTO {
    String typeCode;
    String description;
    BigDecimal thresholdValue;
    String thresholdField;
}