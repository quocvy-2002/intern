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
@Table(name = "carbon_tree")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CarbonTree {

    @Id
    @Column(name = "record_id", columnDefinition = "BINARY(16)")
    UUID recordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tree_id")
    Tree tree;

    @Column(name = "carbon_kg", precision = 10, scale = 2)
    BigDecimal carbonKg;

    @Column(name = "calculated_at", nullable = false)
    LocalDateTime calculatedAt;
}
