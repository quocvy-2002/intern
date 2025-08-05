package com.example.demo.repository;

import com.example.demo.model.entity.UHooMeasurementData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UHooMeasurementDataRepository extends JpaRepository<UHooMeasurementData, Long> {
}