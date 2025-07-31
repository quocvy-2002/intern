package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "device_status_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceStatusLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false, length = 100)
    private String deviceId;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "value", columnDefinition = "TEXT")
    private String value;

    @Column(name = "timestamp")
    private Long timestamp;

    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
}