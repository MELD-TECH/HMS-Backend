package com.hms.identity.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hms.identity.entity.Permission;

public interface PermissionRepository
        extends JpaRepository<Permission, UUID> {

    Optional<Permission> findByCode(
            String code
    );

    boolean existsByCode(
            String code
    );

    @Query("""
        SELECT DISTINCT p
        FROM Permission p
        LEFT JOIN FETCH p.roles
        WHERE p.id = :id
    """)
    Optional<Permission> findByIdWithRoles(
            @Param("id") UUID id
    );
}