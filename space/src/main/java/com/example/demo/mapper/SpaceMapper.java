package com.example.demo.mapper;

import com.example.demo.dto.request.CreatSpaceRequest;
import com.example.demo.dto.request.UpdateSpaceRequest;
import com.example.demo.dto.response.SpaceResponse;
import com.example.demo.entity.Space;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SpaceMapper {
    @Mapping(target = "spaceName", source = "spaceName")
    @Mapping(target = "spaceType.spaceTypeId", source = "spaceTypeId")
    @Mapping(target = "parent.spaceId", source = "parentId")
    Space toSpace(CreatSpaceRequest request);

    SpaceResponse toSpaceResponse(Space space);

    @Mapping(target = "spaceType.spaceTypeId", source = "spaceTypeId")
    @Mapping(target = "parent.spaceId", source = "parentId")
    void updateSpace(@MappingTarget Space space, UpdateSpaceRequest request);
}