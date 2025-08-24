package com.example.demo.model.entity.tree;
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
@Table(name = "carbon_snapshot")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CarbonSnapshot {

    @Id
    @Column(name = "snapshot_id", columnDefinition = "BINARY(16)")
    UUID snapshotId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    Zone zone;

    @Column(name = "total_carbon_kg", precision = 10, scale = 2)
    BigDecimal totalCarbonKg;

    @Column(name = "tree_count")
    Integer treeCount;

    @Column(name = "calculated_at", nullable = false)
    LocalDateTime calculatedAt;

    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;
}
