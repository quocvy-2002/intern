package com.example.demo.controller;

import com.example.demo.service.DeviceStatusSchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/devices")
public class DeviceStatusController {

    private final DeviceStatusSchedulerService schedulerService;

    @PostMapping("/fetch-status")
    public ResponseEntity<String> fetchDeviceStatus(@RequestParam String deviceId) {
        schedulerService.fetchAndSaveDeviceStatus(deviceId);
        return ResponseEntity.ok("Đã gọi và lưu trạng thái cho deviceId: " + deviceId);
    }
}