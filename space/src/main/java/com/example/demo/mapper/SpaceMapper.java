package com.example.demo.mapper;

import com.example.demo.dto.request.CreatSpaceRequest;
import com.example.demo.dto.request.UpdateSpaceRequest;
import com.example.demo.dto.response.SpaceResponse;
import com.example.demo.entity.Space;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SpaceMapper {
    Space toSpace(CreatSpaceRequest request);
    SpaceResponse toSpaceResponse(Space space);
    void updateSpace(@MappingTarget Space space, UpdateSpaceRequest request);
}
