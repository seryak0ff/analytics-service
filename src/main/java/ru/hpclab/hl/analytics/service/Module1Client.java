package ru.hpclab.hl.analytics.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.hpclab.hl.analytics.model.Download;
import ru.hpclab.hl.analytics.model.User;
import ru.hpclab.hl.analytics.service.statistics.ObservabilityService;

import java.util.List;
import java.util.UUID;

@Service
public class Module1Client {
    private final RestTemplate restTemplate;
    private final String module1ServiceUrl;
    private final ObjectMapper objectMapper;
    private final ObservabilityService observabilityService;


    public Module1Client(ObservabilityService observabilityService,
                         RestTemplate restTemplate,
                         @Value("${module1.service.url}") String module1ServiceUrl,
                         ObjectMapper objectMapper) {
        this.observabilityService = observabilityService;
        this.restTemplate = restTemplate;
        this.module1ServiceUrl = module1ServiceUrl;
        this.objectMapper = objectMapper;
    }

    public List<Download> getAllDownloads() {
        this.observabilityService.start(getClass().getSimpleName() + ":getAllDownloads");
        String response = restTemplate.getForObject(
            module1ServiceUrl + "/downloads",
            String.class
        );
        this.observabilityService.stop(getClass().getSimpleName() + ":getAllDownloads");
        try {
            return objectMapper.readValue(response, new TypeReference<List<Download>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse downloads", e);
        }
    }

    public User getUser(UUID userId) {
        this.observabilityService.start(getClass().getSimpleName() + ":getUser");
        String response = restTemplate.getForObject(
            module1ServiceUrl + "/users/" + userId,
            String.class
        );

        this.observabilityService.stop(getClass().getSimpleName() + ":getUser");
        try {
            return objectMapper.readValue(response, User.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse user", e);
        }
    }
} 