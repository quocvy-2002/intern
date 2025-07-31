package com.example.demo.controller;

import com.example.demo.model.dto.response.ApiResponse;
import com.example.demo.model.dto.response.DeviceStateResponse;
import com.example.demo.model.dto.response.OperationLogResponse;
import com.example.demo.model.dto.response.ReportLogResponse;
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

    @GetMapping("/{deviceId}/operation-logs")
    public ApiResponse<OperationLogResponse> getOperationLogs(
            @PathVariable String deviceId,
            @RequestParam(name = "type", defaultValue = "1") String type,
            @RequestParam(name = "start_time") long startTime,
            @RequestParam(name = "end_time") long endTime,
            @RequestParam(name = "query_type", defaultValue = "1") String queryType,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        return ApiResponse.<OperationLogResponse>builder()
                .result(deviceService.getOperationLogs(deviceId, type, startTime, endTime, queryType, size))
                .build();
    }

    @GetMapping("/{deviceId}/report-logs")
    public ApiResponse<ReportLogResponse> getReportLogs(
            @PathVariable String deviceId,
            @RequestParam String codes,
            @RequestParam(name = "start_time") long startTime,
            @RequestParam(name = "end_time") long endTime) {
        return ApiResponse.<ReportLogResponse>builder()
                .result(deviceService.getReportingLogs(deviceId, codes, startTime, endTime))
                .build();
    }
}
