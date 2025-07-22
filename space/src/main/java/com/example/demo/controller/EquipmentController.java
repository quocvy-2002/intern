package com.example.demo.controller;

import com.example.demo.dto.request.CreateEquipmentRequest;
import com.example.demo.dto.request.UpdateEquipmentRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.EquipmentResponse;
import com.example.demo.service.EquipmentService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
@RestController
@RequestMapping("/api/equipments")
public class EquipmentController {
    EquipmentService equipmentService;

    @PostMapping
    ApiResponse<EquipmentResponse> createEquipment(@Valid @RequestBody CreateEquipmentRequest request){
        return ApiResponse.<EquipmentResponse>builder()
                .result(equipmentService.createEquipment(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<EquipmentResponse>> createEquipment(){
        return ApiResponse.<List<EquipmentResponse>>builder()
                .result(equipmentService.getAllEquipments())
                .build();
    }

    @GetMapping("/{equipmentId}")
    ApiResponse<EquipmentResponse> getEquipment(@PathVariable Integer equipmentId) {
        return ApiResponse.<EquipmentResponse>builder()
                .result(equipmentService.getEquipmentById(equipmentId))
                .build();
    }

    @PutMapping("/{equipmentId}")
    ApiResponse<EquipmentResponse> updateEquipment(
            @PathVariable Integer equipmentId,
            @Valid @RequestBody UpdateEquipmentRequest request){
        return ApiResponse.<EquipmentResponse>builder()
                .result(equipmentService.updateEquipment(equipmentId, request))
                .build();
    }

    @DeleteMapping("/{equipmentId}")
    void deleteEquipment(@PathVariable Integer equipmentId) {
        equipmentService.deleteEquipment(equipmentId);
    }

    @GetMapping("/{spaceId}")
    ApiResponse<List<EquipmentResponse>> getAllEquipment(@PathVariable Integer spaceId) {
        return ApiResponse.<List<EquipmentResponse>>builder()
                .result(equipmentService.getEquipmentsBySpaceId(spaceId))
                .build();
    }
}
