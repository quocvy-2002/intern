package com.example.demo.model.entity.tree;

import com.example.demo.model.enums.HealthStatus;
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
@Table(name = "measurement")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Measurement {

    @Id
    @Column(name = "measurement_id", columnDefinition = "BINARY(16)")
    UUID measurementId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tree_id")
    Tree tree;

    @Column(name = "dbh_cm", precision = 6, scale = 2)
    BigDecimal dbhCm;

    @Column(name = "height_m", precision = 6, scale = 2)
    BigDecimal heightM;

    @Column(name = "canopy_diameter_m", precision = 6, scale = 2)
    BigDecimal canopyDiameterM;

    @Enumerated(EnumType.STRING)
    @Column(name = "health_status", length = 20)
    HealthStatus healthStatus;

    @Column(name = "measured_at", nullable = false)
    LocalDateTime measuredAt;

    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;
}
