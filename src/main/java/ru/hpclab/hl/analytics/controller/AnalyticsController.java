package ru.hpclab.hl.analytics.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hpclab.hl.analytics.model.DownloadStatistics;
import ru.hpclab.hl.analytics.service.AnalyticsService;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/university-statistics")
    public ResponseEntity<List<DownloadStatistics>> getUniversityDownloadStatistics() {
        return ResponseEntity.ok(analyticsService.getUniversityDownloadStatistics());
    }
} 