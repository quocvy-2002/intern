package com.example.demo.controller;

import com.example.demo.model.dto.equipment.EquipmentCreateDTO;
import com.example.demo.model.dto.equipment.EquipmentDTO;
import com.example.demo.model.dto.equipment.EquipmentUpdateDTO;
import com.example.demo.model.dto.response.ApiResponse;
import com.example.demo.model.enums.Status;
import com.example.demo.service.EquipmentService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
@RestController
@RequestMapping("/api/equipments")
public class EquipmentController {
    EquipmentService equipmentService;

    @PostMapping
    ApiResponse<EquipmentDTO> createEquipment(@Valid @RequestBody EquipmentCreateDTO request){
        return ApiResponse.<EquipmentDTO>builder()
                .result(equipmentService.createEquipment(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<EquipmentDTO>> createEquipment(){
        return ApiResponse.<List<EquipmentDTO>>builder()
                .result(equipmentService.getAllEquipments())
                .build();
    }

    @GetMapping("/by-id/{equipmentId}")
    ApiResponse<EquipmentDTO> getEquipment(@PathVariable Integer equipmentId) {
        return ApiResponse.<EquipmentDTO>builder()
                .result(equipmentService.getEquipmentById(equipmentId))
                .build();
    }

    @PutMapping("/{equipmentId}")
    ApiResponse<EquipmentDTO> updateEquipment(
            @PathVariable Integer equipmentId,
            @Valid @RequestBody EquipmentUpdateDTO request){
        return ApiResponse.<EquipmentDTO>builder()
                .result(equipmentService.updateEquipment(equipmentId, request))
                .build();
    }

    @DeleteMapping("/{equipmentId}")
    void deleteEquipment(@PathVariable Integer equipmentId) {
        equipmentService.deleteEquipment(equipmentId);
    }

    @GetMapping("/by-space/{spaceId}")
    ApiResponse<List<EquipmentDTO>> getAllEquipment(@PathVariable Integer spaceId) {
        return ApiResponse.<List<EquipmentDTO>>builder()
                .result(equipmentService.getEquipmentsBySpaceId(spaceId))
                .build();
    }

    @PostMapping("/update-status")
    public ApiResponse<Void> updateEquipmentStatus(
            @RequestParam Integer equipmentId,
            @RequestParam Status status,
            @RequestParam(required = false) BigDecimal currentPowerKW) {
        equipmentService.updateStatus(equipmentId, status, currentPowerKW);
        return ApiResponse.<Void>builder().message("Update successful").build();
    }
}
