package com.example.demo.mapper;

import com.example.demo.dto.request.CreateEquipmentValueRequest;
import com.example.demo.dto.request.UpdateEquipmentValueRequest;
import com.example.demo.dto.response.EquipmentValueResponse;
import com.example.demo.entity.EquipmentValue;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EquipmentValueMapper {
    EquipmentValue toEquipmentValue(CreateEquipmentValueRequest request);
    EquipmentValueResponse toEquipmentValueResponse(EquipmentValue equipmentValue);
    void updateEquipmentValue(@MappingTarget Integer equipmentValueId, UpdateEquipmentValueRequest request);
}
