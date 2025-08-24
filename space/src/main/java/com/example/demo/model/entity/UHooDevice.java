package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "uhoo_device")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UHooDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    Long id;

    @Column(name = "device_id", nullable = false, unique = true)
    String macAddress;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "location", nullable = false)
    String location;

    @Column(name = "created_at")
    Date createdAt;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<UHooMeasurementData> measurementDataList;
}