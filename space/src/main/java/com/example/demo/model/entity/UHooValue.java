package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UHooValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long uHooValueId;

    @Column(nullable = false)
    Double value;

    @Column(nullable = false)
    LocalDateTime calculatedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uHooDeviceId", nullable = false)
    UHooDevice uHooDevice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uHooUnitId", nullable = false)
    UHooUnit uHooUnit;
}