package com.example.demo.model.dto.space;

import com.example.demo.model.dto.response.SpaceResponse;
import com.example.demo.model.dto.response.SpaceTypeResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpaceDTO {
        Integer spaceId;
        String spaceName;
        SpaceTypeResponse spaceType;
        SpaceTypeResponse spaceTypeResponse;
        List<SpaceDTO> children;
}
