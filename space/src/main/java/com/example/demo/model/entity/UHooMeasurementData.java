package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "uhoo_measurement_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UHooMeasurementData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", referencedColumnName = "device_id", nullable = false)
    UHooDevice device;

    @Column(name = "timestamp", nullable = false)
    Timestamp timestamp;

    @Column(name = "temperature", precision = 5, scale = 2)
    BigDecimal temperature;

    @Column(name = "humidity", precision = 5, scale = 2)
    BigDecimal humidity;

    @Column(name = "pm2_5", precision = 6, scale = 2)
    BigDecimal pm25;

    @Column(name = "co2", precision = 6, scale = 2)
    BigDecimal co2;

    @Column(name = "tvoc", precision = 6, scale = 2)
    BigDecimal tvoc;

    @Column(name = "air_pressure", precision = 6, scale = 2)
    BigDecimal airPressure;

    @Column(name = "ozone", precision = 6, scale = 2)
    BigDecimal ozone;

    @Column(name = "carbon_monoxide", precision = 6, scale = 2)
    BigDecimal carbonMonoxide;

    @Column(name = "nitrogen_dioxide", precision = 6, scale = 2)
    BigDecimal nitrogenDioxide;

    @Column(name = "noise", precision = 6, scale = 2)
    BigDecimal noise;

    @Column(name = "light", precision = 6, scale = 2)
    BigDecimal light;
}