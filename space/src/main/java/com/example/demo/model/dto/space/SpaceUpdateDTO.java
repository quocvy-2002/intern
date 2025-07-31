package com.example.demo.model.dto.space;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpaceUpdateDTO {
    @NotBlank(message = "SPACE_IS_REQUIRED")
    Integer spaceTypeId;

    @NotBlank(message = "SPACE_IS_REQUIRED")
    Integer parentId;
}
