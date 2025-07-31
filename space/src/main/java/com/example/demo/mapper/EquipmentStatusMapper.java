package com.example.demo.mapper;

import com.example.demo.model.dto.status.StatusCreateDTO;
import com.example.demo.model.dto.status.StatusDTO;
import com.example.demo.model.dto.status.StatusUpdateDTO;
import com.example.demo.model.entity.EquipmentStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EquipmentStatusMapper {
    @Mapping(target = "statusName", source = "statusName")
    @Mapping(target = "equipmentType.equipmentTypeId", source = "equipmentTypeId")
    EquipmentStatus toEquipmentStatus(StatusCreateDTO request);

    StatusDTO toEquipmentStatusResponse(EquipmentStatus equipmentStatus);

    @Mapping(target = "statusName", source = "statusName")
    void updateEquipmentStatus(@MappingTarget EquipmentStatus equipmentStatus, StatusUpdateDTO request);
}