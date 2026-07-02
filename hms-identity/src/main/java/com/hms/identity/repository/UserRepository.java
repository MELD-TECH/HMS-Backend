package com.hms.identity.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.hms.identity.entity.User;

public interface UserRepository
        extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(
            String email
    );

    Optional<User> findByUsername(
            String username
    );

    boolean existsByUsername(
            String username
    );

    boolean existsByEmail(
            String email
    );

    Page<User> findByUsernameContainingIgnoreCase(
            String username,
            Pageable pageable
    );

    @Query("""
        SELECT DISTINCT u
        FROM User u
        LEFT JOIN FETCH u.roles r
        LEFT JOIN FETCH r.permissions
        WHERE u.username = :username
    """)
    Optional<User> findByUsernameWithRolesAndPermissions(
            @Param("username") String username
    );

    @Query("""
        SELECT DISTINCT u
        FROM User u
        LEFT JOIN FETCH u.roles
        WHERE u.id = :id
    """)
    Optional<User> findByIdWithRoles(
            @Param("id") UUID id
    );
    
    @Modifying
    @Transactional
    @Query("""
    update User u
    set u.failedLoginAttempts = :attempts
    where u.id = :userId
    """)
    void updateFailedAttempts(
            UUID userId,
            Integer attempts);
   
    List<User> findByAccountLockedTrue();
    
    @Query("""
    		select u
    		from User u
    		where
    		    u.accountLocked = true
    		and
    		    u.lockExpiresAt <= CURRENT_TIMESTAMP
    		""")
    		List<User> findAccountsToUnlock();
    
}