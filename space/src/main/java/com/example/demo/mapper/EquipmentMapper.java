package com.example.demo.mapper;

import com.example.demo.dto.request.CreateEquipmentRequest;
import com.example.demo.dto.request.UpdateEquipmentRequest;
import com.example.demo.dto.response.EquipmentResponse;
import com.example.demo.entity.Equipment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EquipmentMapper {
    Equipment toEquipment(CreateEquipmentRequest request);
    EquipmentResponse toEquipmentResponse(Equipment equipment);
    void updateEquipment(@MappingTarget Equipment equipment , UpdateEquipmentRequest request);
}
