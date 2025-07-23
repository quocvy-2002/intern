package com.example.demo.controller;

import com.example.demo.dto.request.CreateEquipmentValueRequest;
import com.example.demo.dto.request.UpdateEquipmentValueRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.EquipmentValueResponse;
import com.example.demo.service.EquipmentValueService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
@RestController
@RequestMapping("/api/equipment-values")
public class EquipmentValueController {

    EquipmentValueService equipmentValueService;

    @PostMapping
    public ApiResponse<EquipmentValueResponse> create(@RequestBody CreateEquipmentValueRequest request) {
        return ApiResponse.<EquipmentValueResponse>builder()
                .result(equipmentValueService.createEquipmentValue(request))
                .build();
    }

    @PutMapping("/{equipmentValueId}")
    public ApiResponse<EquipmentValueResponse> update(@PathVariable Integer equipmentValueId,
                                                      @RequestBody UpdateEquipmentValueRequest request) {
        return ApiResponse.<EquipmentValueResponse>builder()
                .result(equipmentValueService.updateEquipmentValue(equipmentValueId, request))
                .build();
    }

    @DeleteMapping("/{equipmentValueId}")
    public ApiResponse<EquipmentValueResponse> delete(@PathVariable Integer equipmentValueId) {
        return ApiResponse.<EquipmentValueResponse>builder()
                .result(equipmentValueService.deleteEquipmentValue(equipmentValueId))
                .build();
    }

    @GetMapping("/{equipmentValueId}")
    public ApiResponse<EquipmentValueResponse> getById(@PathVariable Integer equipmentValueId) {
        return ApiResponse.<EquipmentValueResponse>builder()
                .result(equipmentValueService.getEquipmentValueByEquipmentId(equipmentValueId))
                .build();
    }

    @GetMapping
    public ApiResponse<List<EquipmentValueResponse>> getAll() {
        return ApiResponse.<List<EquipmentValueResponse>>builder()
                .result(equipmentValueService.getAll())
                .build();
    }
}