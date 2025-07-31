package com.example.demo.mapper;

import com.example.demo.model.dto.response.SpaceResponse;
import com.example.demo.model.dto.space.SpaceCreateDTO;
import com.example.demo.model.dto.space.SpaceDTO;
import com.example.demo.model.dto.space.SpaceUpdateDTO;
import com.example.demo.model.entity.Space;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SpaceMapper {
    @Mapping(target = "spaceName", source = "spaceName")
    @Mapping(target = "spaceType.spaceTypeId", source = "spaceTypeId")
    @Mapping(target = "parent.spaceId", source = "parentId")
    Space toSpace(SpaceCreateDTO request);

    SpaceDTO toSpaceResponse(Space space);

    @Mapping(target = "spaceType.spaceTypeId", source = "spaceTypeId")
    @Mapping(target = "parent.spaceId", source = "parentId")
    void updateSpace(@MappingTarget Space space, SpaceUpdateDTO request);
}