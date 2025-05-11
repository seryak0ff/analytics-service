package ru.hpclab.hl.analytics.crash;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CoreCrashScheduler {

    private final WebApiKillerClient killerClient;

    @Scheduled(fixedRate = 10000)
    public void crashRandomPod() {
        killerClient.crashCoreService();
    }
}
