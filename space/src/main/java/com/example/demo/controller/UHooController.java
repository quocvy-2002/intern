package com.example.demo.controller;

import com.example.demo.model.dto.response.ApiResponse;
import com.example.demo.model.dto.response.DeviceResponse;
import com.example.demo.model.dto.response.UHooMeasurementDataResponse;
import com.example.demo.service.UHooService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/uhoo")
@RequiredArgsConstructor
public class UHooController {

    private final UHooService uHooService;

    @PostMapping("/token")
    public ApiResponse<String> fetchToken() {
        String token = uHooService.getToken();
        return ApiResponse.<String>builder()
                .result(token)
                .build();
    }

    @GetMapping("/devices")
    public ApiResponse<List<DeviceResponse>> getDeviceList() {
        List<DeviceResponse> devices = uHooService.getDeviceList();
        return ApiResponse.<List<DeviceResponse>>builder()
                .result(devices)
                .build();
    }

    @PostMapping("/devices/{macAddress}/data")
    public ApiResponse<UHooMeasurementDataResponse> getSensorData(@PathVariable String macAddress) {
        UHooMeasurementDataResponse data = uHooService.getSensorData(macAddress);
        return ApiResponse.<UHooMeasurementDataResponse>builder()
                .result(data)
                .build();
    }
}