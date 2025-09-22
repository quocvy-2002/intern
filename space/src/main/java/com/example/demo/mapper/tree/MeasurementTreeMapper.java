package com.example.demo.mapper.tree;

import com.example.SmartBuildingBackend.model.dto.tree.measurement.MeasurementDTO;
import com.example.SmartBuildingBackend.model.dto.tree.measurement.MeasurementUpdateDTO;
import com.example.SmartBuildingBackend.model.dto.tree.tree.TreeCreateDTO;
import com.example.SmartBuildingBackend.model.entity.tree.MeasurementTree;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MeasurementTreeMapper {

    // Tạo MeasurementTree từ TreeCreateDTO
    @Mapping(target = "measurementId", ignore = true)
    @Mapping(target = "tree", ignore = true) // set trong service
    @Mapping(target = "leafAreaM2", ignore = true)
    @Mapping(target = "biomassKg", ignore = true)
    @Mapping(target = "carbonKg", ignore = true)
    @Mapping(target = "co2AbsorbedKg", ignore = true)
    @Mapping(target = "o2ReleasedKg", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    MeasurementTree toMeasurement(TreeCreateDTO request);

    // Entity -> DTO
    @Mapping(source = "measurementId", target = "measurementId")
    @Mapping(source = "tree.treeId", target = "treeId")
    @Mapping(source = "tree.code", target = "code")
    @Mapping(source = "leafAreaM2", target = "leafAreaM2")
    @Mapping(source = "biomassKg", target = "biomassKg")
    @Mapping(source = "carbonKg", target = "carbonKg")
    @Mapping(source = "co2AbsorbedKg", target = "co2AbsorbedKg")
    @Mapping(source = "o2ReleasedKg", target = "o2ReleasedKg")
    @Mapping(source = "healthStatus", target = "healthStatus")
    MeasurementDTO toMeasurementDTO(MeasurementTree measurement);

    // Update Measurement từ DTO
    @Mapping(target = "measurementId", ignore = true)
    @Mapping(target = "tree", ignore = true)
    @Mapping(target = "leafAreaM2", ignore = true)
    @Mapping(target = "biomassKg", ignore = true)
    @Mapping(target = "carbonKg", ignore = true)
    @Mapping(target = "co2AbsorbedKg", ignore = true)
    @Mapping(target = "o2ReleasedKg", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateMeasurement(@MappingTarget MeasurementTree measurement, MeasurementUpdateDTO request);
}
