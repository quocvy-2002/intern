package com.example.demo.utils;

import com.example.demo.configuration.websocket.QEnergyProperties;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.model.entity.QEnergy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QEnergyUtils {

    WebClient webClient;
    QEnergyProperties props;

    public void getAccessToken(){
        try {
            Map<String,Object> response = webClient.post()
                    .uri(props.getHost()+props.getLoginEndpoint())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of(
                        "role" ,props.getRole(),
                            "password", props.getPassword(),
                            "email", props.getEmail()))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            if (response == null || !response.containsKey("access_token")) {
                System.out.println("Failed to get access token of QEnergy");
            }
            props.setAccessToken((String) response.get("access_token"));
        }
        catch (Exception e){
            throw new AppException(ErrorCode.FAILED_TO_GET_TOKEN);
        }
    }

    public BigDecimal getCurrentEnergyConsumptionBySiteId(Integer  siteId) {
        if (props.getAccessToken() == null || props.getAccessToken() .isEmpty()) {
            getAccessToken();
        }

        try {
            Map<String, Object> response = webClient.get()
                    .uri(props.getHost() + props.getSiteEndpoint() + siteId + "/")
                    .header("Authorization", "Bearer " + props.getAccessToken())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || !response.containsKey("live_power") || response.get("live_power") == null) {
                throw new AppException(ErrorCode.FAILED_TO_GET_LIVE_POWER);
            }

            return new BigDecimal(response.get("live_power").toString());
        } catch (Exception e) {
            throw new AppException(ErrorCode.FAILED_TO_GET_LIVE_POWER);
        }
    }



}
