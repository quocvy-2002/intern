package com.example.demo.mapper.tree;

import com.example.SmartBuildingBackend.model.dto.tree.zone.ZoneCreateDTO;
import com.example.SmartBuildingBackend.model.dto.tree.zone.ZoneDTO;
import com.example.SmartBuildingBackend.model.dto.tree.zone.ZoneUpdateDTO;
import com.example.SmartBuildingBackend.model.entity.tree.Zone;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ZoneMapper {

    ZoneDTO toZoneDTO(Zone zone);

    @Mapping(target = "zoneId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Zone toZone(ZoneCreateDTO request);

    @Mapping(target = "zoneId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateZone(@MappingTarget Zone zone, ZoneUpdateDTO request);
}
