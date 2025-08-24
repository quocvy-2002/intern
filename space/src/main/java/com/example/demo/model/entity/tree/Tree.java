package com.example.demo.model.entity.tree;

import com.example.demo.model.enums.TreeLevel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import org.locationtech.jts.geom.Point;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tree")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Tree {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tree_id", columnDefinition = "BINARY(16)")
    UUID treeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "species_id")
    Species species;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    Zone zone;

    @Enumerated(EnumType.STRING)
    @Column(name = "tree_level", length = 20)
    TreeLevel treeLevel;

    @Column(name = "code", unique = true, length = 50)
    String code;

    @Column(name = "geom", columnDefinition = "point")
    Point geom;

    @Column(name = "planted_date")
    LocalDate plantedDate;

    @Column(name = "deleted_at")
    LocalDateTime deletedAt;

    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;
}
