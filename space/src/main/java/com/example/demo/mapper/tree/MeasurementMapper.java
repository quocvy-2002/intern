package com.example.demo.mapper.tree;

import com.example.demo.model.dto.tree.measurement.MeasurementCreateDTO;
import com.example.demo.model.dto.tree.measurement.MeasurementDTO;
import com.example.demo.model.dto.tree.measurement.MeasurementUpdateDTO;
import com.example.demo.model.entity.tree.Measurement;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MeasurementMapper {
    Measurement toMeasurement(MeasurementCreateDTO request);

    MeasurementDTO toMeasurementDTO(Measurement measurement);

    void updateMeasurement(@MappingTarget Measurement measurement, MeasurementUpdateDTO request);
}
