package com.example.demo.mapper.tree;

import com.example.SmartBuildingBackend.model.dto.tree.tree.TreeCreateDTO;
import com.example.SmartBuildingBackend.model.dto.tree.tree.TreeDTO;
import com.example.SmartBuildingBackend.model.dto.tree.tree.TreeMapDTO;
import com.example.SmartBuildingBackend.model.dto.tree.tree.TreeUpdateDTO;
import com.example.SmartBuildingBackend.model.entity.tree.MeasurementTree;
import com.example.SmartBuildingBackend.model.entity.tree.Tree;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import javax.annotation.Nullable;

@Mapper(componentModel = "spring")
public interface TreeMapper {

    // Tạo Tree từ TreeCreateDTO
    @Mapping(target = "species", ignore = true)
    @Mapping(target = "zone", ignore = true)
    @Mapping(target = "treeId", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true) // không set khi create
    Tree toTree(TreeCreateDTO dto);

    // Map Tree entity sang TreeDTO
    @Mapping(target = "localName", source = "species.localName")
    @Mapping(target = "scientificName", source = "species.scientificName")
    @Mapping(target = "zoneName", source = "zone.zoneName")
    TreeDTO toTreeDTO(Tree tree);

    @Mapping(target = "code", source = "tree.code")
    @Mapping(target = "latitude", source = "tree.latitude")
    @Mapping(target = "longitude", source = "tree.longitude")
    TreeMapDTO toTreeMapDTO(Tree tree, MeasurementTree measurement);

    // Update Tree từ TreeUpdateDTO
    @Mapping(target = "species", ignore = true)
    @Mapping(target = "zone", ignore = true)
    @Mapping(target = "treeId", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateTree(@MappingTarget Tree tree, TreeUpdateDTO request);

    // Map Tree + MeasurementTree sang TreeDTO
    @Mapping(target = "treeId", source = "tree.treeId")
    @Mapping(target = "localName", source = "tree.species.localName")
    @Mapping(target = "scientificName", source = "tree.species.scientificName")
    @Mapping(target = "zoneName", source = "tree.zone.zoneName")
    @Mapping(target = "latitude", source = "tree.latitude")
    @Mapping(target = "longitude", source = "tree.longitude")
    @Mapping(target = "imgUrl", source = "tree.imgUrl")
    @Mapping(target = "plantedDate", source = "tree.plantedDate")
    @Mapping(target = "createdAt", source = "measurement.createdAt")
    @Mapping(target = "girthCm", source = "measurement.girthCm")
    @Mapping(target = "heightM", source = "measurement.heightM")
    @Mapping(target = "canopyDiameterM", source = "measurement.canopyDiameterM")
    @Mapping(target = "healthStatus", source = "measurement.healthStatus")
    @Mapping(target = "measuredAt", source = "measurement.measuredAt")
    @Mapping(target = "leafAreaM2", source = "measurement.leafAreaM2")
    @Mapping(target = "measurementId", source = "measurement.measurementId")
    TreeDTO toDTO(Tree tree, MeasurementTree measurement);

    @AfterMapping
    default void handleNullMeasurement(@MappingTarget TreeDTO treeDTO, @Nullable MeasurementTree measurement) {
        if (measurement == null) {
            treeDTO.setGirthCm(null);
            treeDTO.setHeightM(null);
            treeDTO.setCanopyDiameterM(null);
            treeDTO.setHealthStatus(null);
            treeDTO.setMeasuredAt(null);
            treeDTO.setLeafAreaM2(null);
            treeDTO.setMeasurementId(null);
        }
    }
}
