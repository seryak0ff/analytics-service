package ru.hpclab.hl.analytics.cache;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.hpclab.hl.analytics.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

// кэш сервис универсальность
@Component
public class UserCache {
    private final Map<UUID, User> cache = new HashMap<>();
    private Long hits = 0L;
    private Long misses = 0L;

    public User get(UUID userId) {
        User user = cache.get(userId);
        if (user != null) {
            hits++;
            return user;
        }
        misses++;
        return null;
    }

    public void put(UUID userId, User user) {
        cache.put(userId, user);
    }

    public void clear() {
        cache.clear();
    }

    public int size() {
        return cache.size();
    }

    @Scheduled(fixedRateString = "${cache.stats.print.interval:10000}") // По умолчанию 10 секунд
    public void printStats() {
        System.out.println("User Cache Stats:");
        System.out.println("Size: " + size());
        System.out.println("Hits: " + hits);
        System.out.println("Misses: " + misses);
        System.out.println("Hit rate: " +
                (hits + misses > 0 ?
                        (double) hits / (hits + misses) : 0));
    }
}
