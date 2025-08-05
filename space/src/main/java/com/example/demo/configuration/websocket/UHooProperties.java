package com.example.demo.configuration.websocket;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@ConfigurationProperties(prefix = "uhoo")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UHooProperties {
    String clientId;
    String accessToken;
    String generateTokenURL;
    String deviceList;
    String deviceData;
}