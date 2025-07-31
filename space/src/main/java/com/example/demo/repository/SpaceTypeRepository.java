package com.example.demo.repository;

import com.example.demo.model.entity.SpaceType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpaceTypeRepository extends JpaRepository<SpaceType, Integer> {

    SpaceType findBySpaceTypeId(Integer spaceTypeId);
}
