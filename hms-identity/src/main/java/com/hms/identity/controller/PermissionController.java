package com.hms.identity.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.identity.dto.CreatePermissionRequest;
import com.hms.identity.dto.PermissionResponse;
import com.hms.identity.service.PermissionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController {

    private final PermissionService service;

    public PermissionController(
    		PermissionService service) {

        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PermissionResponse>
    createPermission(

            @Valid
            @RequestBody
            CreatePermissionRequest request) {

        return ResponseEntity.ok(
                service.createPermission(
                        request));
    }
    
    @GetMapping
    @PreAuthorize(
            "hasAuthority('PERMISSION_VIEW')"
    )
    public ResponseEntity<List<PermissionResponse>>
    getPermissions() {

        return ResponseEntity.ok(
                service.getPermissions()
        );
    }

}