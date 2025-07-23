package com.example.demo.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "equipment_type")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EquipmentType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "equipmentTypeId")
    Integer equipmentTypeId;

    @Column(name = "equipmentTypeName")
    String equipmentTypeName;

    @OneToMany(mappedBy = "equipmentType", cascade = CascadeType.ALL, orphanRemoval = true)
    List<EquipmentStatus> equipmentStatus;

}
