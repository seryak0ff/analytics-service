package ru.hpclab.hl.analytics.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hpclab.hl.analytics.model.Download;
import ru.hpclab.hl.analytics.model.DownloadStatistics;
import ru.hpclab.hl.analytics.model.User;
import ru.hpclab.hl.analytics.cache.UserCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.UUID;


@Service
public class AnalyticsService {
    private final Module1Client module1Client;
    private final UserCache userCache;
    @Autowired
    private ObjectMapper objectMapper;

    public AnalyticsService(Module1Client module1Client, UserCache userCache) {
        this.module1Client = module1Client;
        this.userCache = userCache;
    }

    public Map<String, Map<String, Map<String, Long>>> getUniversityDownloadStatistics() {
        List<Download> downloads = module1Client.getAllDownloads();
        Map<String, Map<String, Map<String, Long>>> result = new TreeMap<>();

        for (Download download : downloads) {
            String monthName = download.getDownloadDate().getMonth().name();
            UUID userId = download.getUserId();
            User user = userCache.get(userId);

            if (user == null) {
                user = module1Client.getUser(userId);
                if (user != null) {
                    userCache.put(userId, user);
                }
            }

            String university = user != null ? user.getUniversity() : "Unknown";
            String format = download.getFormat().name();

            result
                    .computeIfAbsent(monthName, k -> new HashMap<>())
                    .computeIfAbsent(university, k -> new HashMap<>())
                    .merge(format, 1L, Long::sum);
        }

        return result;
    }
}
//@Service
//public class AnalyticsService {
//    private final Module1Client module1Client;
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    public AnalyticsService(Module1Client module1Client) {
//        this.module1Client = module1Client;
//    }
//
//    public Map<String, Map<String, Map<String, Long>>> getUniversityDownloadStatistics() {
//        List<Download> downloads = module1Client.getAllDownloads();
//        Map<String, Map<String, Map<String, Long>>> result = new TreeMap<>();
//
//        for (Download download : downloads) {
//            String monthName = download.getDownloadDate().getMonth().name();
//            User user = module1Client.getUser(download.getUserId());
//            String university = user != null ? user.getUniversity() : "Unknown";
//            String format = download.getFormat().name();
//
//            result
//                    .computeIfAbsent(monthName, k -> new HashMap<>())
//                    .computeIfAbsent(university, k -> new HashMap<>())
//                    .merge(format, 1L, Long::sum);
//        }
//
//        return result;
//    }
//}

//    public List<DownloadStatistics> getUniversityDownloadStatistics() {
//        List<Download> downloads = module1Client.getAllDownloads();
//        Map<String, Map<String, Map<String, Long>>> result = new TreeMap<>();
//
//        for (Download download : downloads) {
//            String monthName = download.getDownloadDate().getMonth().name();
//            User user = module1Client.getUser(download.getUserId());
//            String university = user != null ? user.getUniversity() : "Unknown";
//            String format = download.getFormat().name();
//
//            result.computeIfAbsent(monthName, k -> new HashMap<>())
//                    .computeIfAbsent(university, k -> new HashMap<>())
//                    .merge(format, 1L, Long::sum);
//        }
//
//        List<DownloadStatistics> statistics = new ArrayList<>();
//        result.forEach((month, universityData) ->
//                universityData.forEach((university, formatCounts) ->
//                        statistics.add(new DownloadStatistics(month, university, formatCounts))
//                )
//        );
//
//        return statistics;
//    }
