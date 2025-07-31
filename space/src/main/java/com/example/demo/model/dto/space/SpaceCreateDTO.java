package com.example.demo.model.dto.space;


import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpaceCreateDTO {
    @NotBlank(message = "SPACE_IS_REQUIRED")
    String spaceName;

    @NotBlank(message = "SPACE_IS_REQUIRED")
    Integer spaceTypeId;

    @NotBlank(message = "SPACE_IS_REQUIRED")
    Integer parentId;
}
