package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "equipment_value")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EquipmentValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "equipmentValueId")
    Integer equipmentValueId;

    @Column(name = "equipmentValue")
    BigDecimal equipmentValue;
}
