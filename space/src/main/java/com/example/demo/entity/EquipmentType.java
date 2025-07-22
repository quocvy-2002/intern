package com.example.demo.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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

}
