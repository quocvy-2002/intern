package com.example.demo.model.dto.tree.tree;

import com.example.demo.model.enums.TreeLevel;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TreeDTO {
    UUID treeId;
    UUID speciesId;
    UUID zoneId;
    String code;
    Double latitude;
    Double longitude;
    TreeLevel treeLevel;
    LocalDate plantedDate;
    LocalDateTime deletedAt;
    LocalDateTime createdAt;
}