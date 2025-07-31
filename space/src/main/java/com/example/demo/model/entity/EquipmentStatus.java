package com.example.demo.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "equipment_status")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EquipmentStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "statusId")
    Integer statusId;

    @Column(name = "statusName")
    String statusName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipmentTypeId")
    @JsonIgnore
    EquipmentType equipmentType;

    @OneToMany(mappedBy = "equipmentStatus", cascade = CascadeType.ALL, orphanRemoval = true)
    List<EquipmentStatusLog> equipmentStatusLogs;
}