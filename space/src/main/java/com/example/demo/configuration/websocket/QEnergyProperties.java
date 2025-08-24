package com.example.demo.configuration.websocket;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "qenergy")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QEnergyProperties {

    String role;
    String password;
    String email;
    String loginEndpoint;
    String host;
    String siteEndpoint;
    String accessToken;

}