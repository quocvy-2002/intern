package com.example.demo.model.dto.spacetype;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpaceTypeUpdateDTO {
    @NotBlank(message = "SPACE_TYPE_IS_REQUIRED")
    String spaceTypeName;

    @NotBlank(message = "SPACE_TYPE_IS_REQUIRED")
    String spaceTypeLevel;
}
