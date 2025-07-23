package com.example.demo.mapper;

import com.example.demo.dto.request.CreateEquipmentStatusRequest;
import com.example.demo.dto.request.UpdateEquipmentStatusRequest;
import com.example.demo.dto.response.EquipmentStatusResponse;
import com.example.demo.entity.EquipmentStatus;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EquipmentStatusMapper {
    EquipmentStatus toEquipmentStatus(CreateEquipmentStatusRequest request);
    EquipmentStatusResponse toEquipmentStatusResponse(EquipmentStatus equipmentStatus);
    void updateEquipmentStatus(@MappingTarget EquipmentStatus equipmentStatus, UpdateEquipmentStatusRequest request);
}
