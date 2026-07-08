package com.hms.identity.password.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.hms.common.exception.PasswordExpiredException;
import com.hms.identity.entity.User;
import com.hms.identity.security.event.PasswordExpiredEvent;
import com.hms.identity.security.publisher.SecurityEventPublisher;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordExpiryService {

    private final SecurityEventPublisher publisher;

    public void validate(User user) {

        if (user.getPasswordExpiresAt() == null) {
            return;
        }

        if (user.getPasswordExpiresAt()
                .isAfter(LocalDateTime.now())) {

            return;
        }

        publisher.publish(

                new PasswordExpiredEvent(

                        user.getUsername(),

                        user.getId().toString()));

        throw new PasswordExpiredException();

    }

}