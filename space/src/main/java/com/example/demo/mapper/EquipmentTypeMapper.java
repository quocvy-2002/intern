package com.example.demo.mapper;

import com.example.demo.model.dto.equipmenttype.ETypeCreateDTO;
import com.example.demo.model.dto.equipmenttype.ETypeDTO;
import com.example.demo.model.dto.equipmenttype.ETypeUpdateDTO;
import com.example.demo.model.entity.EquipmentType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EquipmentTypeMapper {
    @Mapping(target = "equipmentTypeName", source = "equipmentTypeName")
    EquipmentType toEquipmentType(ETypeCreateDTO request);

    ETypeDTO toEquipmentTypeResponse(EquipmentType equipmentType);

    @Mapping(target = "equipmentTypeName", source = "equipmentTypeName")
    void updateEquipmentType(@MappingTarget EquipmentType equipmentType, ETypeUpdateDTO request);
}