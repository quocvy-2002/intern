package com.example.demo.mapper.tree;

import com.example.SmartBuildingBackend.model.dto.tree.species.SpeciesCreateDTO;
import com.example.SmartBuildingBackend.model.dto.tree.species.SpeciesDTO;
import com.example.SmartBuildingBackend.model.dto.tree.species.SpeciesUpdateDTO;
import com.example.SmartBuildingBackend.model.entity.tree.Species;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SpeciesMapper {
    Species toSpecies(SpeciesCreateDTO request);

    SpeciesDTO toSpeciesDTO(Species species);

    void updateSpecies(@MappingTarget Species species, SpeciesUpdateDTO request);
}
