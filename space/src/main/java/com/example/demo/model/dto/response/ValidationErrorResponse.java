package com.example.demo.model.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ValidationErrorResponse {
    Integer code;
    List<FieldErrorDTO> errors;
}