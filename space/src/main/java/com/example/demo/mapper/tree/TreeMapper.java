package com.example.demo.mapper.tree;

import com.example.demo.model.dto.tree.tree.TreeCreateDTO;
import com.example.demo.model.dto.tree.tree.TreeDTO;
import com.example.demo.model.dto.tree.tree.TreeUpdateDTO;
import com.example.demo.model.entity.tree.Tree;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface TreeMapper {
    @Mapping(target = "species", ignore = true) // sẽ set trong service
    @Mapping(target = "zone", ignore = true)    // sẽ set trong service
    @Mapping(target = "geom", ignore = true)    // sẽ set thủ công
    Tree toTree(TreeCreateDTO dto);

    @Mapping(target = "speciesId", source = "species.speciesId")
    @Mapping(target = "zoneId", source = "zone.zoneId")
    @Mapping(target = "latitude", expression = "java(tree.getGeom() != null ? tree.getGeom().getY() : null)")
    @Mapping(target = "longitude", expression = "java(tree.getGeom() != null ? tree.getGeom().getX() : null)")
    TreeDTO toTreeDTO(Tree tree);

    void updateTree(@MappingTarget Tree tree, TreeUpdateDTO request);

    Tree toTree(TreeDTO treeDTO);
}
