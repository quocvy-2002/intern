package com.example.demo.model.dto.response;

import com.example.demo.model.entity.Result;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TuyaTokenResponse {
    boolean success;
    Result result;
    long t;
    String tid;
}
