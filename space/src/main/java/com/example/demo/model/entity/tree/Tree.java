package com.example.demo.model.entity.tree;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity

@Table(name = "tree", indexes = {
        @Index(name = "idx_tree_coordinates", columnList = "latitude,longitude"),
        @Index(name = "idx_tree_code", columnList = "code")
})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Tree {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tree_id")
    Long treeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "species_id")
    Species species;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    Zone zone;

    @Column(name = "code", unique = true, length = 50)
    String code;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "planted_date")
    LocalDate plantedDate;

    @Column(name = "deleted_at")
    LocalDateTime deletedAt;

    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;

    @Column(name = "url_img")
    String imgUrl;
}