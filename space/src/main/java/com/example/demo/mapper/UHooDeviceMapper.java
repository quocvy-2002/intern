package com.example.demo.mapper;

import com.example.demo.model.dto.response.DeviceResponse;
import com.example.demo.model.dto.response.UHooDeviceResponse;
import com.example.demo.model.entity.UHooDevice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UHooDeviceMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "measurementDataList", ignore = true)
    UHooDevice toUHooDevice(DeviceResponse dto);

    UHooDeviceResponse toDeviceResponse(UHooDevice entity);


}