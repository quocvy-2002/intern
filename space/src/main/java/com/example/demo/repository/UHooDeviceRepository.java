package com.example.demo.repository;

import com.example.demo.model.entity.UHooDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UHooDeviceRepository extends JpaRepository<UHooDevice, Long> {
    Optional<UHooDevice> findByMacAddress(String macAddress);
}