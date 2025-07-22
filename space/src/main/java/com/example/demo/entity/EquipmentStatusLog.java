package com.example.demo.entity;

import com.example.demo.enums.Status;
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

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    Status status;

    @Column(name = "powerConsumptionKW")
    BigDecimal powerConsumptionKW;

}
