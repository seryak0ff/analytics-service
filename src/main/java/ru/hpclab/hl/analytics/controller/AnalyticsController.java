package ru.hpclab.hl.analytics.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hpclab.hl.analytics.model.DownloadStatistics;
import ru.hpclab.hl.analytics.service.AnalyticsService;
import java.util.Map;
import java.util.List;
import ru.hpclab.hl.analytics.service.statistics.ObservabilityService;


@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    private final AnalyticsService analyticsService;
    private final ObservabilityService observabilityService;

    public AnalyticsController(ObservabilityService observabilityService, AnalyticsService analyticsService) {
        this.observabilityService = observabilityService;
        this.analyticsService = analyticsService;
    }

    @GetMapping("/university-statistics")
    public ResponseEntity<Map<String, Map<String, Map<String, Long>>>> getUniversityDownloadStatistics() {
        this.observabilityService.start(getClass().getSimpleName() + ":getUniversityDownloadStatistics - Controller");
        ResponseEntity<Map<String, Map<String, Map<String, Long>>>> temp = ResponseEntity.ok(analyticsService.getUniversityDownloadStatistics());
        this.observabilityService.stop(getClass().getSimpleName() + ":getUniversityDownloadStatistics - Controller");
        return temp;
    }

//    @GetMapping("/university-statistics")
//    public ResponseEntity<List<DownloadStatistics>> getUniversityDownloadStatistics() {
//        return ResponseEntity.ok(analyticsService.getUniversityDownloadStatistics());
//    }
} 