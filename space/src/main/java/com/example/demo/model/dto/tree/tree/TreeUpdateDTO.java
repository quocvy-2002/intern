package com.example.demo.model.dto.tree.tree;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TreeUpdateDTO {
    String localName;

    String zoneName;

    @DecimalMin(value = "-90.0", message = "LATITUDE_OUT_OF_RANGE")
    @DecimalMax(value = "90.0", message = "LATITUDE_OUT_OF_RANGE")
    Double latitude;

    @DecimalMin(value = "-180.0", message = "LONGITUDE_OUT_OF_RANGE")
    @DecimalMax(value = "180.0", message = "LONGITUDE_OUT_OF_RANGE")
    Double longitude;

    String imgUrl;

    LocalDate plantedDate;

    LocalDateTime deletedAt;
}