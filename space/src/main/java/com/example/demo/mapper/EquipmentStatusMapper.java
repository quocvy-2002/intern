package com.example.demo.mapper;

import com.example.demo.dto.request.CreateEquipmentStatusRequest;
import com.example.demo.dto.request.UpdateEquipmentStatusRequest;
import com.example.demo.dto.response.EquipmentStatusResponse;
import com.example.demo.entity.EquipmentStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EquipmentStatusMapper {
    @Mapping(target = "statusName", source = "statusName")
    @Mapping(target = "equipmentType.equipmentTypeId", source = "equipmentTypeId")
    EquipmentStatus toEquipmentStatus(CreateEquipmentStatusRequest request);

    EquipmentStatusResponse toEquipmentStatusResponse(EquipmentStatus equipmentStatus);

    @Mapping(target = "statusName", source = "statusName")
    void updateEquipmentStatus(@MappingTarget EquipmentStatus equipmentStatus, UpdateEquipmentStatusRequest request);
}