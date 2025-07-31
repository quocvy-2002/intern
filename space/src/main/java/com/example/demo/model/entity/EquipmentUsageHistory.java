package com.example.demo.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "equipment_usage_history")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EquipmentUsageHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "historyId")
    Integer historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "equipmentId",referencedColumnName = "equipmentId")
    Equipment equipment;

    @Column(name = "startTime")
    LocalDateTime startTime;

    @Column(name ="endTime")
    LocalDateTime endTime;

    @Column(name = "totalPowerConsumption")
    BigDecimal totalPowerConsumption;

}
