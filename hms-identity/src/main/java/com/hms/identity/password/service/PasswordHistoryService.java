package com.hms.identity.password.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hms.common.exception.BusinessException;
import com.hms.identity.entity.User;
import com.hms.identity.password.config.PasswordPolicyProperties;
import com.hms.identity.password.entity.PasswordHistory;
import com.hms.identity.password.repository.PasswordHistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PasswordHistoryService {

    private final PasswordHistoryRepository repository;

    private final PasswordEncoder passwordEncoder;

    private final PasswordPolicyProperties properties;

    /**
     * Save current password into history.
     */
    public void saveHistory(
            User user) {

        PasswordHistory history =
                PasswordHistory.builder()
                        .user(user)
                        .passwordHash(
                                user.getPasswordHash())
                        .changedAt(
                                user.getPasswordChangedAt())
                        .build();

        repository.save(history);
    }

    /**
     * Prevent password reuse.
     */
    public void validatePasswordReuse(
            User user,
            String rawPassword) {

        List<PasswordHistory> history =
                repository.findTopByUserIdOrderByChangedAtDesc(
                        user.getId(), PageRequest.of(0, properties.getHistoryCount()));

        int limit = Math.min(
                properties.getHistoryCount(),
                history.size());

        for (int i = 0; i < limit; i++) {

            PasswordHistory item =
                    history.get(i);

            if (passwordEncoder.matches(
                    rawPassword,
                    item.getPasswordHash())) {

                throw new BusinessException(
                        "Password has been used previously");

            }

        }

    }

}