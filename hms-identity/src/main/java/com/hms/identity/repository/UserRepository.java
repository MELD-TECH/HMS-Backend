package com.hms.identity.repository;

import com.hms.identity.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository
        extends JpaRepository<User, UUID> {

	@Query("""
		    SELECT DISTINCT u
		    FROM User u
		    LEFT JOIN FETCH u.roles r
		    LEFT JOIN FETCH r.permissions
		    WHERE u.username = :username
		""")
		Optional<User> findByUsernameWithRolesAndPermissions(
		        @Param("username") String username);
    Optional<User> findByEmail(
            String email
    );

    boolean existsByUsername(
            String username
    );

    boolean existsByEmail(
            String email
    );
    
    Page<User> findByUsernameContainingIgnoreCase(
            String username,
            Pageable pageable);
}