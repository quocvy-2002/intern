package com.example.demo.repository;

import com.example.demo.model.entity.EquipmentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EquipmentTypeRepository extends JpaRepository<EquipmentType, Integer> {
    Optional<EquipmentType> findByEquipmentTypeName(String equipmentTypeName);
    Optional<EquipmentType> findByEquipmentTypeId(Integer equipmentTypeId);

}
