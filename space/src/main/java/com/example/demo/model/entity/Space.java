package com.example.demo.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "space")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Space {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spaceId")
    Integer spaceId;

    @Column(name = "spaceName", nullable = false)
    String spaceName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spaceTypeId", nullable = false)
    @JsonIgnore
    SpaceType spaceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentId")
    @JsonIgnore
    Space parent;

    @Column(name = "qEnergySiteId", unique = true)
    Integer  qEnergySiteId;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Space> children;

    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Equipment> equipments;

    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference(value = "qEnergy-space")
    @Builder.Default
    List<QEnergy> qEnergies = new ArrayList<>();
}
