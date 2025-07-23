package com.example.demo.repository;

import com.example.demo.entity.EquipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EquipmentStatusRepository extends JpaRepository<EquipmentStatus, Integer> {
    List<EquipmentStatus> findByEquipmentType_EquipmentTypeId(Integer equipmentTypeId);
    Optional<EquipmentStatus> findByStatusId(Integer statusId);
}