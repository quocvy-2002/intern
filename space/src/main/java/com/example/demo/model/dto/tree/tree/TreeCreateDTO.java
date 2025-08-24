package com.example.demo.model.dto.tree.tree;

import com.example.demo.model.enums.TreeLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TreeCreateDTO {
    @NotNull(message = "IS_REQUIRED")
    UUID speciesId;

    @NotNull(message = "IS_REQUIRED")
    UUID zoneId;

    @NotNull(message = "IS_REQUIRED")
    Double latitude;

    @NotNull(message = "IS_REQUIRED")
    Double longitude;

    @NotBlank(message = "IS_REQUIRED")
    TreeLevel treeLevel;

    LocalDate plantedDate;
}