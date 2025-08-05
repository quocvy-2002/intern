package com.example.demo.controller;

import com.example.demo.model.dto.response.ApiResponse;
import com.example.demo.model.dto.response.DeviceStateResponse;
import com.example.demo.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping("/{deviceId}/status")
    public ApiResponse<DeviceStateResponse> getDeviceFrozenState(@PathVariable String deviceId) {
        return ApiResponse.<DeviceStateResponse>builder()
                .result(deviceService.getDeviceFrozenState(deviceId))
                .build();
    }

}
