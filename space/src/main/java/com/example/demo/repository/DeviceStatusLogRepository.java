package com.example.demo.repository;

import com.example.demo.model.entity.DeviceStatusLog;
import org.springframework.data.repository.CrudRepository;

public interface DeviceStatusLogRepository extends CrudRepository<DeviceStatusLog, Long> {
}
