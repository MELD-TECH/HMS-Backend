package com.hms.identity.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.identity.dto.CreatePermissionRequest;
import com.hms.identity.dto.PermissionResponse;
import com.hms.identity.dto.UpdatePermissionRequest;
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
    @PreAuthorize(
    	    "hasAuthority('PERMISSION_CREATE')"
    	)
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
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_VIEW')")
    public ResponseEntity<PermissionResponse>
    getPermission(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                service.getPermission(id)
        );
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_UPDATE')")
    public ResponseEntity<PermissionResponse>
    updatePermission(
            @PathVariable UUID id,
            @RequestBody UpdatePermissionRequest request) {

        return ResponseEntity.ok(
                service.updatePermission(
                        id,
                        request
                )
        );
    }

}