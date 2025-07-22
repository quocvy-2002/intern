package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "spaceType")
public class SpaceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spaceTypeId")
    Integer spaceTypeId;

    @Column(name = "spaceTypeName", nullable = false)
    String spaceTypeName;

    @Column(name = "spaceTypeLevel")
    String spaceTypeLevel;

    @OneToMany(mappedBy = "spaceType", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Space> spaces;


}
