package com.example.demo.repository;

import com.example.demo.model.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EquipmentRepository extends JpaRepository<Equipment, Integer> {
    Optional<Equipment> findByEquipmentId(Integer equipmentId);
    void deleteByEquipmentId(Integer equipmentId);
    List<Equipment> findBySpace_SpaceId(Integer spaceId);
}
