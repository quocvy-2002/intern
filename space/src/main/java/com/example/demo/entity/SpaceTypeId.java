package com.example.demo.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpaceTypeId implements Serializable {

    private String spaceTypeName;
    private String spaceTypeLevel;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpaceTypeId)) return false;
        SpaceTypeId that = (SpaceTypeId) o;
        return Objects.equals(spaceTypeName, that.spaceTypeName) &&
                Objects.equals(spaceTypeLevel, that.spaceTypeLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spaceTypeName, spaceTypeLevel);
    }
}


