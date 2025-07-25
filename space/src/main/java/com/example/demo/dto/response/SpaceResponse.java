package com.example.demo.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpaceResponse {
    Integer spaceId;
    String spaceName;
    SpaceTypeResponse spaceType;
    List<SpaceResponse> children;
}
