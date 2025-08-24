package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UHooUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long uHooUnitId;

    String description;

    @Column(nullable = false)
    String unit;

    @OneToMany(mappedBy = "uHooUnit", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    List<UHooValue> uHooValues = new ArrayList<>();
}