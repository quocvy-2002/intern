package com.example.demo.model.dto.tree.tree;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TreeUpdateDTO {
    @NotNull(message = "IS_REQUIRED")
    UUID speciesId;

    @NotNull(message = "IS_REQUIRED")
    UUID zoneId;

    @NotNull(message = "IS_REQUIRED")
    Double latitude;

    @NotNull(message = "IS_REQUIRED")
    Double longitude;

    LocalDate plantedDate;

    LocalDateTime deletedAt;
}