package com.example.demo.mapper;

import com.example.demo.model.dto.response.QEnergyDTO;
import com.example.demo.model.entity.QEnergy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QEnergyMapper {
    QEnergyDTO toQEnergyDto (QEnergy qEnergy);
}
