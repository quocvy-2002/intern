package com.example.demo.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name ="equipment_status_log")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EquipmentStatusLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer statusId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "equipmentId", referencedColumnName = "equipmentId")
    Equipment equipment;

    @Column(name = "timestamp")
    LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status")
    @JsonIgnore
    EquipmentStatus equipmentStatus;

    @Column(name = "powerConsumptionKW")
    BigDecimal powerConsumptionKW;

}
