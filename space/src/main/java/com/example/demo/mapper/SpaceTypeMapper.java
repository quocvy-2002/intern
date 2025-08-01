package com.example.demo.mapper;




import com.example.demo.model.dto.spacetype.SpaceTypeCreateDTO;
import com.example.demo.model.dto.spacetype.SpaceTypeDTO;
import com.example.demo.model.dto.spacetype.SpaceTypeUpdateDTO;
import com.example.demo.model.entity.SpaceType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SpaceTypeMapper {
    @Mapping(target = "spaceTypeName", source = "spaceTypeName")
    @Mapping(target = "spaceTypeLevel", source = "spaceTypeLevel")
    SpaceType toSpaceType(SpaceTypeCreateDTO request);

    SpaceTypeDTO toSpaceTypeDTO(SpaceType spaceType);

    @Mapping(target = "spaceTypeName", source = "spaceTypeName")
    @Mapping(target = "spaceTypeLevel", source = "spaceTypeLevel")
    void updateSpaceType(@MappingTarget SpaceType spaceType, SpaceTypeUpdateDTO request);
}