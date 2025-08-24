package com.example.demo.model.entity.weather;

import com.example.demo.model.entity.tree.Zone;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "weather_forecast")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WeatherForecast {

    @Id
    @Column(name = "forecast_id", columnDefinition = "BINARY(16)")
    UUID forecastId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    Zone zone;

    @Column(name = "provider", nullable = false, length = 50)
    String provider;

    @Column(name = "api_version", length = 20)
    String apiVersion;

    @Column(name = "retrieved_at", nullable = false)
    LocalDateTime retrievedAt;

    @Column(name = "forecast_time", nullable = false)
    LocalDateTime forecastTime;

    @Column(name = "temperature_c", precision = 5, scale = 2)
    BigDecimal temperatureC;

    @Column(name = "humidity_pct", precision = 5, scale = 2)
    BigDecimal humidityPct;

    @Column(name = "rainfall_mm", precision = 5, scale = 2)
    BigDecimal rainfallMm;

    @Column(name = "wind_speed_ms", precision = 5, scale = 2)
    BigDecimal windSpeedMs;

    @Column(name = "wind_gust_ms", precision = 5, scale = 2)
    BigDecimal windGustMs;

    @Column(name = "conditions", length = 100)
    String conditions;
}
