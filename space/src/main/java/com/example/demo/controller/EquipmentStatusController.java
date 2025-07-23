package com.example.demo.controller;

import com.example.demo.dto.request.CreateEquipmentStatusRequest;
import com.example.demo.dto.request.UpdateEquipmentStatusRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.EquipmentStatusResponse;
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
    public ApiResponse<EquipmentStatusResponse> create(@RequestBody CreateEquipmentStatusRequest request) {
        return ApiResponse.<EquipmentStatusResponse>builder()
                .result(equipmentStatusService.createEquipmentStatus(request))
                .build();
    }

    @PutMapping("/{statusId}")
    public ApiResponse<EquipmentStatusResponse> update(@PathVariable Integer statusId,
                                                       @RequestBody UpdateEquipmentStatusRequest request) {
        return ApiResponse.<EquipmentStatusResponse>builder()
                .result(equipmentStatusService.updateEquipmentStatus(statusId, request))
                .build();
    }

    @DeleteMapping("/{statusId}")
    public ApiResponse<EquipmentStatusResponse> delete(@PathVariable Integer statusId) {
        return ApiResponse.<EquipmentStatusResponse>builder()
                .result(equipmentStatusService.deleteEquipmentStatus(statusId))
                .build();
    }

    @GetMapping("/{statusId}")
    public ApiResponse<EquipmentStatusResponse> getById(@PathVariable Integer statusId) {
        return ApiResponse.<EquipmentStatusResponse>builder()
                .result(equipmentStatusService.getEquipmentStatus(statusId))
                .build();
    }

    @GetMapping("/equipment-type/{equipmentTypeId}")
    public ApiResponse<List<EquipmentStatusResponse>> getByEquipmentType(@PathVariable Integer equipmentTypeId) {
        return ApiResponse.<List<EquipmentStatusResponse>>builder()
                .result(equipmentStatusService.getEquipmentStatusByEquiment(equipmentTypeId))
                .build();
    }

    @GetMapping
    public ApiResponse<List<EquipmentStatusResponse>> getAll() {
        return ApiResponse.<List<EquipmentStatusResponse>>builder()
                .result(equipmentStatusService.getAllEquipmentStatuses())
                .build();
    }
}
