package ru.hpclab.hl.analytics.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Download {
    private UUID id;
    private UUID userId;
    private UUID articleId;
    private LocalDateTime downloadDate;
    private DownloadFormat format;

    public enum DownloadFormat {
        PDF, HTML
    }
} 