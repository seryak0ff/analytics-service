package ru.hpclab.hl.analytics.crash;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class WebApiKillerClient {

    private final WebClient webClient;

    public void crashCoreService() {
        webClient.post()
                .uri("http://main-service:8080/internal/crash")
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
