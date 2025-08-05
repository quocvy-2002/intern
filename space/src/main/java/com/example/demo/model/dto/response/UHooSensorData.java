package com.example.demo.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UHooSensorData {
    BigDecimal temperature;
    BigDecimal humidity;
    BigDecimal pm25;
    BigDecimal co2;
    BigDecimal tvoc;
    BigDecimal airPressure;
    BigDecimal ozone;
    @JsonProperty("co")
    BigDecimal carbonMonoxide;
    @JsonProperty("no2")
    BigDecimal nitrogenDioxide;
    @JsonProperty("sound")
    BigDecimal noise;
    BigDecimal light;
    Timestamp timestamp;
}
