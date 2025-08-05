package com.example.demo.mapper;

import com.example.demo.model.dto.response.UHooMeasurementDataResponse;
import com.example.demo.model.dto.response.UHooSensorData;
import com.example.demo.model.entity.UHooMeasurementData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UHooMeasurementDataMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "device", ignore = true)
    UHooMeasurementData toEntity(UHooSensorData dto);

    UHooMeasurementDataResponse toDto(UHooMeasurementData entity);
}