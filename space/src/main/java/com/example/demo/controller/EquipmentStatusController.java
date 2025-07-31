package com.example.demo.controller;

import com.example.demo.model.dto.status.StatusRequest;
import com.example.demo.model.dto.response.ApiResponse;
import com.example.demo.model.dto.status.StatusCreateDTO;
import com.example.demo.model.dto.status.StatusDTO;
import com.example.demo.model.dto.status.StatusUpdateDTO;
import com.example.demo.service.EquipmentStatusService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
@RestController
@RequestMapping("/api/equipment-statuses")
public class EquipmentStatusController {

    EquipmentStatusService equipmentStatusService;

    @PostMapping
    public ApiResponse<StatusDTO> create(@RequestBody StatusCreateDTO request) {
        return ApiResponse.<StatusDTO>builder()
                .result(equipmentStatusService.createEquipmentStatus(request))
                .build();
    }

    @PostMapping("/update-status-history")
    public ApiResponse<String> updateStatusHistory(@RequestBody StatusRequest request) {
        equipmentStatusService.updateStatusHistory(request);
        return ApiResponse.<String>builder()
                .result("Device status updated successfully")
                .build();
    }

    @PutMapping("/{statusId}")
    public ApiResponse<StatusDTO> update(@PathVariable Integer statusId,
                                                       @RequestBody StatusUpdateDTO request) {
        return ApiResponse.<StatusDTO>builder()
                .result(equipmentStatusService.updateEquipmentStatus(statusId, request))
                .build();
    }

    @DeleteMapping("/{statusId}")
    public ApiResponse<StatusDTO> delete(@PathVariable Integer statusId) {
        return ApiResponse.<StatusDTO>builder()
                .result(equipmentStatusService.deleteEquipmentStatus(statusId))
                .build();
    }

    @GetMapping("/{statusId}")
    public ApiResponse<StatusDTO> getById(@PathVariable Integer statusId) {
        return ApiResponse.<StatusDTO>builder()
                .result(equipmentStatusService.getEquipmentStatus(statusId))
                .build();
    }

    @GetMapping("/equipment-type/{equipmentTypeId}")
    public ApiResponse<List<StatusDTO>> getByEquipmentType(@PathVariable Integer equipmentTypeId) {
        return ApiResponse.<List<StatusDTO>>builder()
                .result(equipmentStatusService.getEquipmentStatusByEquiment(equipmentTypeId))
                .build();
    }

    @GetMapping
    public ApiResponse<List<StatusDTO>> getAll() {
        return ApiResponse.<List<StatusDTO>>builder()
                .result(equipmentStatusService.getAllEquipmentStatuses())
                .build();
    }

    @GetMapping("/getLogs")
    public ApiResponse<List<StatusDTO>> getAllLog() {
        return ApiResponse.<List<StatusDTO>>builder()
                .result(equipmentStatusService.getAllLogs())
                .build();
    }
}
