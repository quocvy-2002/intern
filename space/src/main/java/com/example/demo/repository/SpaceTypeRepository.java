package com.example.demo.repository;

import com.example.demo.entity.SpaceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpaceTypeRepository extends JpaRepository<SpaceType, Integer> {

    SpaceType findBySpaceTypeId(Integer spaceTypeId);
}
