package com.example.demo.model.entity.tree;

import com.example.SmartBuildingBackend.model.enums.HealthStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "measurementTree")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MeasurementTree {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "measurement_id")
    Long measurementId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tree_id")
    Tree tree;

    @Column(name = "girth_cm", precision = 6, scale = 2)
    BigDecimal girthCm;

    @Column(name = "height_m", precision = 6, scale = 2)
    BigDecimal heightM;

    @Column(name = "canopy_diameter_m", precision = 6, scale = 2)
    BigDecimal canopyDiameterM;

    @Column(name = "leaf_area_m2", precision = 12, scale = 4)
    BigDecimal leafAreaM2;

    @Column(name = "biomass_kg", precision = 12, scale = 4)
    BigDecimal biomassKg;

    @Column(name = "carbon_kg", precision = 12, scale = 4)
    BigDecimal carbonKg;

    @Column(name = "co2_absorbed_kg", precision = 12, scale = 4)
    BigDecimal co2AbsorbedKg;

    @Column(name = "o2_released_kg", precision = 12, scale = 4)
    BigDecimal o2ReleasedKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "health_status", length = 20)
    HealthStatus healthStatus;

    @Column(name = "measured_at", nullable = false)
    LocalDateTime measuredAt;

    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;

    @Column(name = "water_loss", precision = 12, scale = 4)
    BigDecimal waterLoss;
}
