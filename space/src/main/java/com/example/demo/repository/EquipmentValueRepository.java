package com.example.demo.repository;

import com.example.demo.entity.EquipmentValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EquipmentValueRepository extends JpaRepository<EquipmentValue, Integer> {
    Optional<EquipmentValue> findByEquipmentValueId(Integer equipmentValueId);
    boolean existsByEquipmentValueId(Integer equipmentValueId);
}
