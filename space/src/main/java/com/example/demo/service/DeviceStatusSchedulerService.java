package com.example.demo.service;

import com.example.demo.dto.response.DeviceStateResponse;
import com.example.demo.dto.response.DeviceStatusEntry;
import com.example.demo.entity.DeviceStatusLog;
import com.example.demo.repository.DeviceStatusLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceStatusSchedulerService {

    private final DeviceService deviceService; // service chứa hàm getDeviceFrozenState
    private final DeviceStatusLogRepository logRepository;

    public void fetchAndSaveDeviceStatus(String deviceId) {
        try {
            DeviceStateResponse response = deviceService.getDeviceFrozenState(deviceId);

            if (response.getResult() != null) {
                for (DeviceStatusEntry entry : response.getResult()) {
                    DeviceStatusLog log = DeviceStatusLog.builder()
                            .deviceId(deviceId)
                            .code(entry.getCode())
                            .value(entry.getValue().toString())
                            .timestamp(response.getT())
                            .build();
                    logRepository.save(log);
                }
            }

            log.info("Đã lưu trạng thái thiết bị {} vào DB", deviceId);

        } catch (Exception e) {
            log.error("Lỗi khi gọi API và lưu trạng thái cho deviceId {}: {}", deviceId, e.getMessage());
        }
    }

    // Gọi mỗi 30 phút
//    @Scheduled(fixedRate = 30 * 60 * 1000) // 30 phút
//    public void autoFetchForAllDevices() {
//        List<String> deviceIds = List.of("abc123", "xyz456");
//        for (String deviceId : deviceIds) {
//            fetchAndSaveDeviceStatus(deviceId);
//        }
//    }
}