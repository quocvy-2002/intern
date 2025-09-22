package com.example.demo.model.dto.tree.tree;

import com.example.SmartBuildingBackend.model.enums.HealthStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TreeCreateDTO {
    @NotBlank(message = "IS_REQUIRED")
    String localName;

    @NotBlank(message = "IS_REQUIRED")
    String zoneName;

    @NotNull(message = "IS_REQUIRED")
    @DecimalMin(value = "-90.0", message = "LATITUDE_OUT_OF_RANGE")
    @DecimalMax(value = "90.0", message = "LATITUDE_OUT_OF_RANGE")
    Double latitude;

    @NotNull(message = "IS_REQUIRED")
    @DecimalMin(value = "-180.0", message = "LONGITUDE_OUT_OF_RANGE")
    @DecimalMax(value = "180.0", message = "LONGITUDE_OUT_OF_RANGE")
    Double longitude;

//    @NotBlank(message = "IS_REQUIRED")
    String imgUrl;

    LocalDate plantedDate;

//    @NotNull(message = "IS_REQUIRED")
    BigDecimal girthCm;

//    @NotNull(message = "IS_REQUIRED")
    BigDecimal heightM;

//    @NotNull(message = "IS_REQUIRED")
    BigDecimal canopyDiameterM;

//    @NotBlank(message = "IS_REQUIRED")
    HealthStatus healthStatus;

//    @NotNull(message = "IS_REQUIRED")
    LocalDateTime measuredAt;

}