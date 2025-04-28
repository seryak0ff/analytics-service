package ru.hpclab.hl.analytics.service.statistics;


import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;



@Service
public class ObservabilityService {
    private static final Instant PENDING_STOP = null;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    private final List<Integer> intervals;
    private final int delay;
    private final Set<Timing> timings = new ConcurrentSkipListSet<>(Comparator.comparing(Timing::getStart));

    public ObservabilityService(List<Integer> intervals, int delay) {
        this.intervals = intervals;
        this.delay = delay;
    }

    public void start(String name) {
        timings.add(new Timing(name, Instant.now(), PENDING_STOP));
    }

    public void stop(String name) {
        Instant stopTime = Instant.now();
        timings.stream()
                .filter(t -> t.getName().equals(name) && t.getStop() == PENDING_STOP)
                .findFirst()
                .ifPresent(timing -> timing.setStop(stopTime));
    }

    @Async
    @Scheduled(fixedDelayString = "${service.statistic.observability.delay}")
    public void getStatistics() {
        Instant now = Instant.now();
        List<Timing> completedTimings = getCompletedTimings(now);

        printStatisticsHeader(now);

        if (!completedTimings.isEmpty()) {
            printStatisticsTable(now, completedTimings);
        }

        printStatisticsFooter();
    }

    private List<Timing> getCompletedTimings(Instant now) {
        int maxInterval = intervals.stream().max(Integer::compare).orElse(60);
        removeOldTimings(now, maxInterval);

        return timings.stream()
                .filter(t -> t.getStop() != PENDING_STOP)
                .toList();
    }

    private void removeOldTimings(Instant now, int maxInterval) {
        timings.removeIf(t -> now.minusSeconds(maxInterval).isAfter(t.getStart()));
    }

    private void printStatisticsHeader(Instant now) {
        System.out.println("\n=== PERFORMANCE STATISTICS (" + TIME_FORMATTER.format(now) + ") ===");
        System.out.println("Showing average execution times for different periods:");
        System.out.println("METHOD                   | LAST 10s | LAST 30s | LAST 60s");
        System.out.println("---------------------------------------------");
    }

    private void printStatisticsTable(Instant now, List<Timing> timings) {
        Map<String, Map<Integer, Double>> stats = calculateStatistics(now, timings);

        stats.forEach((name, intervalStats) -> {
            System.out.printf("%-25s |", name);
            intervals.forEach(interval -> {
                Double avg = intervalStats.get(interval);
                System.out.printf(" %7.3fs |", avg != null ? avg : 0.0);
            });
            System.out.println();
        });
    }

    private Map<String, Map<Integer, Double>> calculateStatistics(Instant now, List<Timing> timings) {
        Map<String, Map<Integer, Double>> stats = new TreeMap<>();

        timings.stream()
                .map(Timing::getName)
                .distinct()
                .forEach(name -> {
                    Map<Integer, Double> intervalStats = new HashMap<>();

                    intervals.forEach(interval -> {
                        double avg = timings.stream()
                                .filter(t -> t.getName().equals(name) &&
                                        !t.getStart().isBefore(now.minusSeconds(interval)))
                                .mapToLong(t -> Duration.between(t.getStart(), t.getStop()).toMillis())
                                .average()
                                .orElse(0.0) / 1000.0;

                        intervalStats.put(interval, avg);
                    });

                    stats.put(name, intervalStats);
                });

        return stats;
    }

    private void printStatisticsFooter() {
        System.out.println("\n=============================================");
    }
}




//public class ObservabilityService {
//    private static final Instant PENDING_STOP = null;
//
//    private final List<Integer> intervals;
//
//    private final int delay;
//
//    private final Set<Timing> timings = new ConcurrentSkipListSet<>(Comparator.comparing(Timing::getStart));
//
//    public ObservabilityService(List<Integer> intervals, int delay) {
//        this.intervals = intervals;
//        this.delay = delay;
//    }
//
//    public void start(String name) {
//        Timing timing = new Timing(name, Instant.now(), PENDING_STOP);
//        timings.add(timing);
//    }
//
//    public void stop(String name) {
//        Instant stopTime = Instant.now();
//        Optional<Timing> timingOpt = timings.stream()
//                .filter(t -> t.getName().equals(name) && t.getStop() == PENDING_STOP)
//                .findFirst();
//
//        if (timingOpt.isPresent()) {
//            Timing timing = timingOpt.get();
//            timing.setStop(stopTime);
//        }
//    }
//
//    private void removeOldTimings(Instant now, int maxInterval) {
//        timings.removeIf(timing -> now.minusSeconds(maxInterval).isAfter(timing.getStart()));
//    }
//
//    private Set<String> getUniqueNamesByTiming(List<Timing> timings) {
//        return timings.stream().map(Timing::getName)
//                .collect(Collectors.toSet());
//    }
//
//    @Async(value = "applicationTaskExecutor")
//    @Scheduled(fixedDelayString = "${service.statistic.observability.delay}")
//    public void getStatistics() {
//        List<Timing> snapshot = new ArrayList<>(timings);
//        Instant now = Instant.now();
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
//                .withZone(ZoneId.systemDefault());
//
//
//        int maxInterval = intervals.stream().max(Integer::compare).get();
//
//        removeOldTimings(now, maxInterval);
//        // копить в хэш мапе потом все выводить
//        Set<String> uniqueNames = getUniqueNamesByTiming(snapshot);
//
//        // сюда собираем статистику и потом выводим единым выводом
//        Map<String, Map<Integer, Double>> stats = new HashMap<>();
//
//        for (String name : uniqueNames) {
//            Map<Integer, Double> intervalStats = new HashMap<>();
//
//            for (int interval : intervals) {
//                List<Timing> filteredTimings = snapshot.stream()
//                        .filter(t -> t.getStop() != PENDING_STOP
//                                && !t.getStart().isBefore(now.minusSeconds(interval))
//                                && !t.getStart().isAfter(now)
//                                && t.getName().equals(name))
//                        .toList();
//
//                if (!filteredTimings.isEmpty()) {
//                    double averageDuration = filteredTimings.stream()
//                            .mapToLong(t -> Duration.between(t.getStart(), t.getStop()).toMillis())
//                            .average().getAsDouble();
//
//                    intervalStats.put(interval, averageDuration / 1000); // секунды
//                }
//            }
//            if (!intervalStats.isEmpty()) {
//                stats.put(name, intervalStats);
//            }
//        }
//
//        // Теперь централизованный вывод:
//        String timestamp = "[" + formatter.format(now) + "]";
//        System.out.println("\nPrint observability statistics in delay: " + delay);
//
//        for (Map.Entry<String, Map<Integer, Double>> entry : stats.entrySet()) {
//            String name = entry.getKey();
//            Map<Integer, Double> intervalStats = entry.getValue();
//
//            for (Map.Entry<Integer, Double> statEntry : intervalStats.entrySet()) {
//                int interval = statEntry.getKey();
//                double avg = statEntry.getValue();
//                System.out.println(timestamp + " - " + interval + " : " + name + " - " + avg + " s.");
//
//
//            }
//        }
//    }
//}