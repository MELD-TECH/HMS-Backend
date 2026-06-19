package com.hms.security.provider;

import org.springframework.security.core.authority.
        SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.*;

import org.springframework.stereotype.Service;

import java.util.List;

//@Service
public class UserDetailsServiceImpl
        implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(
            String username)
            throws UsernameNotFoundException {

        return new User(
                username,
                "$2a$10$n7zYKthqb4KtbhRviWtAsOsyGwFT6KftmoN0oEGE98xfjhN2fCMXW",
                List.of(
                        new SimpleGrantedAuthority(
                                "ROLE_ADMIN"
                        )
                )
        );
    }
}