package com.hms.identity.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hms.identity.entity.Permission;

public interface PermissionRepository
extends JpaRepository<Permission, UUID> {

Optional<Permission> findByCode(
    String code);

boolean existsByCode(
    String code);
}