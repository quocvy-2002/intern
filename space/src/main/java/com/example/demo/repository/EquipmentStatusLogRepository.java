package com.example.demo.repository;

import com.example.demo.entity.Equipment;
import com.example.demo.entity.EquipmentStatusLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EquipmentStatusLogRepository extends JpaRepository<EquipmentStatusLog, Integer> {
    Optional<EquipmentStatusLog> findTopByEquipmentOrderByTimestampDesc(Equipment equipment);
}
