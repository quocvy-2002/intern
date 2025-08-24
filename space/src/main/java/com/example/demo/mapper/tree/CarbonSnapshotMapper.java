package com.example.demo.mapper.tree;
import com.example.demo.model.dto.tree.CarbonSnapshotDTO;
import com.example.demo.model.entity.tree.CarbonSnapshot;
import org.mapstruct.Mapper;
@Mapper(componentModel = "spring")
public interface CarbonSnapshotMapper {
    CarbonSnapshotDTO toDto(CarbonSnapshot carbonSnapshot);
}