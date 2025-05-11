package ru.hpclab.hl.analytics.crash;

import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthCheckController {

    private final HealthEndpoint healthEndpoint;

    public HealthCheckController(HealthEndpoint healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }

    @GetMapping
    public ResponseEntity<String> health() {
        return healthEndpoint.health().getStatus() == Status.UP
                ? ResponseEntity.ok("OK")
                : ResponseEntity.status(503).build();
    }
}