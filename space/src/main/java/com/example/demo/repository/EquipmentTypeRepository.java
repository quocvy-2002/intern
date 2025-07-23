package com.example.demo.repository;

import com.example.demo.entity.EquipmentStatus;
import com.example.demo.entity.EquipmentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EquipmentTypeRepository extends JpaRepository<EquipmentType, Integer> {


}
