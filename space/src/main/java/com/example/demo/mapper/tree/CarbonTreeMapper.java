package com.example.demo.mapper.tree;

import com.example.demo.model.dto.tree.CarbonTreeDTO;
import com.example.demo.model.entity.tree.CarbonTree;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CarbonTreeMapper {
    CarbonTreeDTO toDto(CarbonTree carbonTree);
}