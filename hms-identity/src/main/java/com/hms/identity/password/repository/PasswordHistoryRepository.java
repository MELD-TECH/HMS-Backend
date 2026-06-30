package com.hms.identity.password.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hms.identity.password.entity.PasswordHistory;

public interface PasswordHistoryRepository
        extends JpaRepository<PasswordHistory, UUID> {

	List<PasswordHistory> findTopByUserIdOrderByChangedAtDesc(UUID userId, Pageable pageable);

}