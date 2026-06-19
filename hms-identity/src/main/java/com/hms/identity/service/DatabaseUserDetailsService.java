package com.hms.identity.service;

import com.hms.identity.entity.Permission;
import com.hms.identity.entity.Role;
import com.hms.identity.entity.User;
import com.hms.identity.enums.UserStatus;
import com.hms.identity.repository.UserRepository;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;

import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService
        implements UserDetailsService {

    private final UserRepository repository;

    public DatabaseUserDetailsService(
            UserRepository repository) {

        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(
            String username)
            throws UsernameNotFoundException {

        User user =
                repository.findByUsernameWithRolesAndPermissions(
                        username)
                .orElseThrow(
                        () ->
                                new UsernameNotFoundException(
                                        username
                                )
                );

        return org.springframework.security.core.userdetails.User
                .withUsername(
                        user.getUsername())
                .password(
                        user.getPasswordHash())
                .disabled(
                        user.getStatus()
                                == UserStatus.DISABLED
                )
                .authorities(
                        buildAuthorities(user)
                )
                .build();
    }

    private Collection<? extends GrantedAuthority> buildAuthorities(User user) {

        Set<GrantedAuthority> authorities =
                new HashSet<>();

        for (Role role : user.getRoles()) {

            authorities.add(
                    new SimpleGrantedAuthority(
                            "ROLE_" + role.getName()
                    )
            );

            for (Permission permission :
                    role.getPermissions()) {

                authorities.add(
                        new SimpleGrantedAuthority(
                                permission.getCode()
                        )
                );
            }
        }
        
        return authorities;
    }
}