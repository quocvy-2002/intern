package com.example.demo.mapper;


import com.example.demo.dto.request.CreatSpaceTypeRequest;
import com.example.demo.dto.request.UpdateSpaceTypeRequest;
import com.example.demo.dto.response.SpaceTypeResponse;
import com.example.demo.entity.SpaceType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SpaceTypeMapper {
    SpaceType toSpaceType(CreatSpaceTypeRequest request);
    SpaceTypeResponse toSpaceTypeResponse(SpaceType space);
    void updateSpaceType(@MappingTarget SpaceType spaceType, UpdateSpaceTypeRequest request);

}
