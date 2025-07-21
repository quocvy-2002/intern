package com.example.demo.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "SPACE")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Space {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spaceId")
    Integer spaceId;

    @Column(name = "spaceName", nullable = false)
    String spaceName;

    @Column(name = "spaceTypeName")
    String spaceTypeName;

    @Column(name = "spaceTypeLevel")
    String spaceTypeLevel;

    @Column(name = "parentId")
    Integer parentId;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "spaceTypeName", referencedColumnName = "spaceTypeName", insertable = false, updatable = false),
            @JoinColumn(name = "spaceTypeLevel", referencedColumnName = "spaceTypeLevel", insertable = false, updatable = false)
    })
    private SpaceType spaceType;
}