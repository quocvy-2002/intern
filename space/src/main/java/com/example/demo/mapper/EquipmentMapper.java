package com.example.demo.mapper;

import com.example.demo.model.dto.equipment.EquipmentCreateDTO;
import com.example.demo.model.dto.equipment.EquipmentDTO;
import com.example.demo.model.dto.equipment.EquipmentUpdateDTO;
import com.example.demo.model.dto.request.CreateEquipmentRequest;
import com.example.demo.model.dto.request.UpdateEquipmentRequest;
import com.example.demo.model.entity.Equipment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")

public interface EquipmentMapper {
    @Mapping(target = "equipmentType", ignore = true)
    Equipment toEquipment(EquipmentCreateDTO request);

    EquipmentDTO toEquipmentResponse(Equipment equipment);

    @Mapping(target = "equipmentName", source = "equipmentName")
    @Mapping(target = "space", source = "space")
    void updateEquipment(@MappingTarget Equipment equipment, EquipmentUpdateDTO request);
}
