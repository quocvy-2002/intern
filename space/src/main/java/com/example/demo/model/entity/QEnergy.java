package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "qenergy")
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QEnergy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long qEnergyId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "space_id", nullable = false)
    Space space;

    @Column(nullable = false, precision = 14, scale = 7)
    BigDecimal currentEnergyConsumption;

    @Column(nullable = false, precision = 17, scale = 7)
    BigDecimal totalEnergyConsumption;

    @Column(nullable = false)
    LocalDateTime date;
}