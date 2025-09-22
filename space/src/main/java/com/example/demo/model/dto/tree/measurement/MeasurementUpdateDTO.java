package com.example.demo.model.dto.tree.measurement;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MeasurementUpdateDTO {

    BigDecimal girthCm;

    BigDecimal heightM;

    BigDecimal canopyDiameterM;

    String healthStatus;

    LocalDateTime measuredAt;

    String descriptionStatus;
}
