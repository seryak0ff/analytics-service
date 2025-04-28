package ru.hpclab.hl.analytics.cache;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.hpclab.hl.analytics.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import ru.hpclab.hl.analytics.service.statistics.ObservabilityService;

// кэш сервис универсальность
@Component
public class UserCache {
    private final Map<UUID, User> cache = new HashMap<>();
    private Long hits = 0L;
    private Long misses = 0L;
    private final ObservabilityService observabilityService;

    public UserCache(ObservabilityService observabilityService) {
        this.observabilityService = observabilityService;
    }

    public User get(UUID userId) {
        this.observabilityService.start(getClass().getSimpleName() + ":getUserCache");

        User user = cache.get(userId);
        if (user != null) {
            hits++;
            this.observabilityService.stop(getClass().getSimpleName() + ":getUserCache");
            return user;
        }
        misses++;
        this.observabilityService.stop(getClass().getSimpleName() + ":getUserCache");
        return null;
    }

    public void put(UUID userId, User user) {
        this.observabilityService.start(getClass().getSimpleName() + ":putUserCache");
        cache.put(userId, user);
        this.observabilityService.stop(getClass().getSimpleName() + ":putUserCache");
    }

    public void clear() {
        this.observabilityService.start(getClass().getSimpleName() + ":clearUserCache");
        cache.clear();
        this.observabilityService.stop(getClass().getSimpleName() + ":clearUserCache");
    }

    public int size() {
        this.observabilityService.start(getClass().getSimpleName() + ":sizeUserCache");
        int temp = cache.size();
        this.observabilityService.stop(getClass().getSimpleName() + ":sizeUserCache");
        return temp;
    }

    @Scheduled(fixedRateString = "${cache.stats.print.interval:60000}") // По умолчанию 60 секунд
    public void printStats() {
        this.observabilityService.start(getClass().getSimpleName() + ":printStatsCache");
        System.out.println("--- User Cache Info ---");
        System.out.println("Current size: " + size());
        System.out.println("-----------------------");
//        System.out.println("Hits: " + hits);
//        System.out.println("Misses: " + misses);
//        System.out.println("Hit rate: " +
//                (hits + misses > 0 ?
//                        (double) hits / (hits + misses) : 0));
        this.observabilityService.stop(getClass().getSimpleName() + ":printStatsCache");
    }
}
