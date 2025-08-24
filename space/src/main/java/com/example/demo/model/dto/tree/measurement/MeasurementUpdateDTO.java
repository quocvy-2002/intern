package com.example.demo.model.dto.tree.measurement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "IS_REQUIRED")
    BigDecimal dbhCm;

    @NotNull(message = "IS_REQUIRED")
    BigDecimal heightM;

    @NotNull(message = "IS_REQUIRED")
    BigDecimal canopyDiameterM;

    @NotBlank(message = "IS_REQUIRED")
    String healthStatus;

    @NotNull(message = "IS_REQUIRED")
    LocalDateTime measuredAt;
}
