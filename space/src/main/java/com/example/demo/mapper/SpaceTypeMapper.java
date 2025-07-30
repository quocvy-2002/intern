package com.example.demo.mapper;




import com.example.demo.dto.request.CreatSpaceTypeRequest;
import com.example.demo.dto.request.UpdateSpaceTypeRequest;
import com.example.demo.dto.response.SpaceTypeResponse;
import com.example.demo.entity.SpaceType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SpaceTypeMapper {
    @Mapping(target = "spaceTypeName", source = "spaceTypeName")
    @Mapping(target = "spaceTypeLevel", source = "spaceTypeLevel")
    SpaceType toSpaceType(CreatSpaceTypeRequest request);

    SpaceTypeResponse toSpaceTypeResponse(SpaceType spaceType);

    @Mapping(target = "spaceTypeName", source = "spaceTypeName")
    @Mapping(target = "spaceTypeLevel", source = "spaceTypeLevel")
    void updateSpaceType(@MappingTarget SpaceType spaceType, UpdateSpaceTypeRequest request);
}