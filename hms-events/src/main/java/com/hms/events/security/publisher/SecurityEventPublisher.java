package com.hms.events.security.publisher;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.hms.events.security.SecurityEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SecurityEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publish(SecurityEvent event) {
        publisher.publishEvent(event);
    }
}