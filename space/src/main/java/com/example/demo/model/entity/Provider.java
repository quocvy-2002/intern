package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "provider")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Provider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "providerId")
    Integer providerId;

    @Column(name = "providerName")
    String providerName;

}
