package com.example.demo.mapper.tree;

import com.example.demo.model.dto.tree.zone.ZoneCreateDTO;
import com.example.demo.model.dto.tree.zone.ZoneDTO;
import com.example.demo.model.dto.tree.zone.ZoneUpdateDTO;
import com.example.demo.model.entity.tree.Zone;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ZoneMapper {
    ZoneDTO toZoneDTO(Zone zone);
    Zone toZone(ZoneCreateDTO request);

    @Mapping(target = "zoneId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateZone(@MappingTarget Zone zone, ZoneUpdateDTO request);
}