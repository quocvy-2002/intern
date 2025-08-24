package com.example.demo.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "equipment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "equipmentId")
    Integer equipmentId;

    @Column(name = "equipmentName")
    String equipmentName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spaceId")
    @JsonIgnore
    Space space;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "equipmentTypeId", referencedColumnName = "equipmentTypeId")
    EquipmentType equipmentType;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "providerId", referencedColumnName = "providerId")
    Provider provider;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "equipmentValueId", referencedColumnName = "equipmentValueId")
    EquipmentValue equipmentValue;

}
