package com.hms.identity.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.identity.dto.AssignPermissionRequest;
import com.hms.identity.dto.AssignRoleRequest;
import com.hms.identity.dto.CreateRoleRequest;
import com.hms.identity.dto.RoleResponse;
import com.hms.identity.dto.UpdateRoleRequest;
import com.hms.identity.service.RoleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService service;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    public ResponseEntity<RoleResponse>
    createRole(
            @Valid
            @RequestBody
            CreateRoleRequest request) {

        return ResponseEntity.ok(
                service.createRole(request)
        );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    public ResponseEntity<List<RoleResponse>>
    getRoles() {

        return ResponseEntity.ok(
                service.getRoles()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    public ResponseEntity<RoleResponse>
    getRole(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                service.getRole(id)
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    public ResponseEntity<RoleResponse>
    updateRole(
            @PathVariable UUID id,
            @RequestBody UpdateRoleRequest request) {

        return ResponseEntity.ok(
                service.updateRole(id, request)
        );
    }
    
    @PostMapping("/{roleId}/permissions")
    @PreAuthorize(
            "hasAuthority('ROLE_UPDATE')"
    )
    public ResponseEntity<Void> assignPermissions(
            @PathVariable UUID roleId,
            @RequestBody AssignPermissionRequest request) {

    	System.out.println("Assigning role " + request.permissionId() + " to role" + roleId);
    	
        service.assignPermission(
        		roleId,
                request.permissionId()
        );

        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping(
            "/{roleId}/permissions/{permissionId}"
    )
    @PreAuthorize(
            "hasAuthority('ROLE_UPDATE')"
    )
    public ResponseEntity<Void> removeRole(
            @PathVariable UUID roleId,
            @PathVariable UUID permissionId) {

        service.removePermission(
        		roleId,
        		permissionId
        );

        return ResponseEntity.noContent()
                .build();
    }
}