package com.example.demo.model.dto.tree.tree;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TreeDTO {
    Long treeId;
    String localName;
    String scientificName;
    String zoneName;
    String code;
    Double latitude;
    Double longitude;
    String imgUrl;
    LocalDate plantedDate;
    LocalDateTime createdAt;
    Long measurementId;
    BigDecimal leafAreaM2;
    BigDecimal girthCm;
    BigDecimal heightM;
    BigDecimal canopyDiameterM;
    String healthStatus;
    LocalDateTime measuredAt;

}