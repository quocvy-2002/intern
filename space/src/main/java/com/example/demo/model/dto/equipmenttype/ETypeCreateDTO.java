package com.example.demo.model.dto.equipmenttype;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ETypeCreateDTO {
    @NotBlank(message = "EQUIPMENT_TYPE_IS_REQUIRED")
    String equipmentTypeName;
}
