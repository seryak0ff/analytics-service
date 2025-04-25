package ru.hpclab.hl.analytics.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DownloadStatistics {
    private String month;
    private String university;
    private Map<String, Long> formatCounts;
} 