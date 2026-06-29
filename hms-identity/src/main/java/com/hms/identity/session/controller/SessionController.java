package com.hms.identity.session.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.identity.session.dto.SessionResponse;
import com.hms.identity.session.service.SessionService;
import com.hms.security.util.SecurityUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService service;

    @GetMapping
    public List<SessionResponse> sessions(){

        return service.sessions(
                SecurityUtils.getCurrentUsername());

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> revoke(
            @PathVariable UUID id){

        service.revoke(id);
        
        return ResponseEntity.noContent().build();

    }

    @DeleteMapping
    public ResponseEntity<Void> revokeAll(){

        service.revokeAll(
                SecurityUtils.getCurrentUsername());

        return ResponseEntity.noContent().build();
    }

}
