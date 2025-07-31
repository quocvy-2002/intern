package com.example.demo.model.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.xml.transform.Result;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TuyaResponse {
    private int code;
    private String msg;
    private boolean success;
    private long t;
    private String tid;
    private Result result;
}
