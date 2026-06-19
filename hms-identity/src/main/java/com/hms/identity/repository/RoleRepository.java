package com.hms.identity.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hms.identity.entity.Role;

public interface RoleRepository
extends JpaRepository<Role, UUID> {

Optional<Role> findByName(
    String name);

boolean existsByName(
    String name);
}