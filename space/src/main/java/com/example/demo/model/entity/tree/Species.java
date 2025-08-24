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
@Table(name = "species")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Species {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "species_id", columnDefinition = "BINARY(16)")
    UUID speciesId;

    @Column(name = "scientific_name", nullable = false, unique = true, length = 255)
    String scientificName;

    @Column(name = "local_name", length = 255)
    String localName;

    @Column(name = "wood_density", precision = 6, scale = 3)
    BigDecimal woodDensity;

    @Column(name = "coeff_b0", precision = 10, scale = 4)
    BigDecimal coeffB0;

    @Column(name = "coeff_b1", precision = 10, scale = 4)
    BigDecimal coeffB1;

    @Column(name = "coeff_b2", precision = 10, scale = 4)
    BigDecimal coeffB2;

    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;
}
