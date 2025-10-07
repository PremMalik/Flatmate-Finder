package com.roommatefinder.service;

import com.roommatefinder.repo.AdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CleanupService {
    private final AdRepository adRepo;

    @Scheduled(cron = "0 0 0 * * *") // run at midnight
    public void deleteExpiredAds() {
        var expired = adRepo.findByExpiresAtBefore(Instant.now());
        if (!expired.isEmpty()) {
            adRepo.deleteAll(expired);
            System.out.println("Deleted " + expired.size() + " expired ads");
        }
    }
}
