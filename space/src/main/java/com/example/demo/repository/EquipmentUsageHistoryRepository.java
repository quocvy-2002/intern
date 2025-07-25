package com.example.demo.repository;

import com.example.demo.entity.Equipment;
import com.example.demo.entity.EquipmentUsageHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EquipmentUsageHistoryRepository extends JpaRepository<EquipmentUsageHistory, Integer> {
    Optional<EquipmentUsageHistory> findTopByEquipmentAndEndTimeIsNullOrderByStartTimeDesc(Equipment equipment);
    boolean existsByEquipmentAndEndTimeIsNull(Equipment equipment);
}
