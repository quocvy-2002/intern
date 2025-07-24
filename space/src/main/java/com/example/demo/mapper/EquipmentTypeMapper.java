package com.example.demo.mapper;

import com.example.demo.dto.request.CreateEquipmentTypeRequest;
import com.example.demo.dto.request.UpdateEquipmentTypeRequest;
import com.example.demo.dto.response.EquipmentTypeResponse;
import com.example.demo.entity.EquipmentType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EquipmentTypeMapper {
    EquipmentType toEquipmentType(CreateEquipmentTypeRequest request);
    EquipmentTypeResponse toEquipmentTypeResponse(EquipmentType equipmentType);
    void updateEquipmentType(@MappingTarget EquipmentType equipmentType, UpdateEquipmentTypeRequest request);

}
