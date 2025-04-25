package ru.hpclab.hl.analytics.cache;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.hpclab.hl.analytics.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class UserCache {
    private final Map<UUID, User> cache = new HashMap<>();
    private final AtomicLong hits = new AtomicLong(0);
    private final AtomicLong misses = new AtomicLong(0);

    public User get(UUID userId) {
        User user = cache.get(userId);
        if (user != null) {
            hits.incrementAndGet();
            return user;
        }
        misses.incrementAndGet();
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

    @Scheduled(fixedRateString = "${cache.stats.print.interval:30000}") // По умолчанию 60 секунд
    public void printStats() {
        System.out.println("User Cache Stats:");
        System.out.println("Size: " + size());
        System.out.println("Hits: " + hits.get());
        System.out.println("Misses: " + misses.get());
        System.out.println("Hit rate: " +
                (hits.get() + misses.get() > 0 ?
                        (double) hits.get() / (hits.get() + misses.get()) : 0));
    }
}
