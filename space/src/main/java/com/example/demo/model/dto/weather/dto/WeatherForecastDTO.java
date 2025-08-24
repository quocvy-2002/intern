package com.example.demo.model.dto.weather.dto;

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
public class WeatherForecastDTO {
    UUID forecastId;
    UUID zoneId;
    String provider;
    String apiVersion;
    LocalDateTime retrievedAt;
    LocalDateTime forecastTime;
    BigDecimal temperatureC;
    BigDecimal humidityPct;
    BigDecimal rainfallMm;
    BigDecimal windSpeedMs;
    BigDecimal windGustMs;
    String conditions;
}