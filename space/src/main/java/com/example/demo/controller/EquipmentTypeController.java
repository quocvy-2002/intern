package com.example.demo.controller;

import com.example.demo.model.dto.equipmenttype.ETypeCreateDTO;
import com.example.demo.model.dto.equipmenttype.ETypeDTO;
import com.example.demo.model.dto.equipmenttype.ETypeUpdateDTO;
import com.example.demo.model.dto.response.ApiResponse;
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
    public ApiResponse<ETypeDTO> createEquipmentType(@Valid @RequestBody ETypeCreateDTO request) {
        return ApiResponse.<ETypeDTO>builder()
                .result(equipmentTypeService.createEquipmentType(request))
                .build();
    }

    @GetMapping("/{equipmentTypeId}")
    public ApiResponse<ETypeDTO> getEquipmentTypeById(@PathVariable Integer id) {
        return ApiResponse.<ETypeDTO>builder()
                .result(equipmentTypeService.getEquipmentById(id))
                .build();
    }

    @GetMapping
    public ApiResponse<List<ETypeDTO>> getAllEquipmentTypes() {
        return ApiResponse.<List<ETypeDTO>>builder()
                .result(equipmentTypeService.getAllEquipmentTypes())
                .build();
    }

    @PutMapping("/{equipmentTypeId}")
    public ApiResponse<ETypeDTO> updateEquipmentType(@PathVariable Integer id,
                                                                  @Valid @RequestBody ETypeUpdateDTO request) {
        return ApiResponse.<ETypeDTO>builder()
                .result(equipmentTypeService.updateEquipmentType(id, request))
                .build();
    }
}
