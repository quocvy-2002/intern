package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@IdClass(SpaceTypeId.class)
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "spaceType")
public class SpaceType {
    @Id
    @Column(name = "spaceTypeName")
    private String spaceTypeName;

    @Id
    @Column(name = "spaceTypeLevel")
    private String spaceTypeLevel;
}
