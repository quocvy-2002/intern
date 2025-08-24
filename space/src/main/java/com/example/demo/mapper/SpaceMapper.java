package com.example.demo.mapper;



import com.example.demo.model.dto.space.SpaceCreateDTO;
import com.example.demo.model.dto.space.SpaceDTO;
import com.example.demo.model.dto.space.SpaceUpdateDTO;
import com.example.demo.model.entity.Space;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SpaceMapper {

    @Mapping(target = "spaceType", ignore = true) // sẽ set thủ công
    @Mapping(target = "parent", ignore = true)
    Space toSpace(SpaceCreateDTO request);

    SpaceDTO toSpaceResponse(Space space);

    @Mapping(target = "spaceType", ignore = true)
    @Mapping(target = "parent", ignore = true)
    void updateSpace(@MappingTarget Space space, SpaceUpdateDTO request);
}