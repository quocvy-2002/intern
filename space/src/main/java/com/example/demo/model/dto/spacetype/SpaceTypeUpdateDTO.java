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
    @NotBlank(message = "IS_REQUIRED")
    String spaceTypeName;

    @NotBlank(message = "IS_REQUIRED")
    String spaceTypeLevel;
}
