package com.hms.identity.security.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hms.identity.entity.User;
import com.hms.identity.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class LoginAttemptService {

    private final UserRepository repository;

    /**
     * Increment failed login attempts.
     */
    public void loginFailed(User user) {

        user.setFailedLoginAttempts(

                user.getFailedLoginAttempts() + 1);

        repository.save(user);
    }

    /**
     * Reset failed login attempts after successful login.
     */
    public void loginSucceeded(User user) {

        user.setFailedLoginAttempts(0);

        repository.save(user);
    }

}