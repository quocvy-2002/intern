package com.example.demo.controller;

import com.example.demo.dto.request.CreateEquipmentTypeRequest;
import com.example.demo.dto.request.UpdateEquipmentTypeRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.EquipmentTypeResponse;
import com.example.demo.service.EquipmentTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment-types")
@RequiredArgsConstructor
public class EquipmentTypeController {

    EquipmentTypeService equipmentTypeService;

    @PostMapping
    public ApiResponse<EquipmentTypeResponse> createEquipmentType(@Valid @RequestBody CreateEquipmentTypeRequest request) {
        return ApiResponse.<EquipmentTypeResponse>builder()
                .result(equipmentTypeService.createEquipmentType(request))
                .build();
    }

    @GetMapping("/{equipmentTypeId}")
    public ApiResponse<EquipmentTypeResponse> getEquipmentTypeById(@PathVariable Integer id) {
        return ApiResponse.<EquipmentTypeResponse>builder()
                .result(equipmentTypeService.getEquipmentById(id))
                .build();
    }

    @GetMapping
    public ApiResponse<List<EquipmentTypeResponse>> getAllEquipmentTypes() {
        return ApiResponse.<List<EquipmentTypeResponse>>builder()
                .result(equipmentTypeService.getAllEquipmentTypes())
                .build();
    }

    @PutMapping("/{equipmentTypeId}")
    public ApiResponse<EquipmentTypeResponse> updateEquipmentType(@PathVariable Integer id,
                                                                  @Valid @RequestBody UpdateEquipmentTypeRequest request) {
        return ApiResponse.<EquipmentTypeResponse>builder()
                .result(equipmentTypeService.updateEquipmentType(id, request))
                .build();
    }
}
