package com.hms.identity.session.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.hms.identity.session.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupJob {

    private final RefreshTokenRepository repository;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanup() {

        repository.deleteExpiredAndRevoked();

    }

}