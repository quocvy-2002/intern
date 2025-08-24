package com.example.demo.repository;

import com.example.demo.model.entity.SpaceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpaceTypeRepository extends JpaRepository<SpaceType, Integer> {

    Optional<SpaceType> findBySpaceTypeId(Integer spaceTypeId);
}
