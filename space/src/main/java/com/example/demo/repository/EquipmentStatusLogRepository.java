package com.example.demo.repository;

import com.example.demo.model.dto.response.EquipmentLogResponse;
import com.example.demo.model.entity.Equipment;
import com.example.demo.model.entity.EquipmentStatusLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EquipmentStatusLogRepository extends JpaRepository<EquipmentStatusLog, Integer> {
    Optional<EquipmentStatusLog> findTopByEquipmentOrderByTimestampDesc(Equipment equipment);

    @Query("SELECT new com.example.demo.model.dto.response.EquipmentLogResponse(" +
            "e.equipmentName, l.timestamp, s.statusName) " +
            "FROM EquipmentStatusLog l " +
            "JOIN l.equipment e " +
            "JOIN l.equipmentStatus s")
    List<EquipmentLogResponse> findAllLogs();

}
