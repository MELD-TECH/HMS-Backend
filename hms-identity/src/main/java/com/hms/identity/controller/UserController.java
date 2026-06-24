package com.hms.identity.controller;

import com.hms.identity.dto.*;

import com.hms.identity.service.UserService;

import jakarta.validation.Valid;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService service;

    public UserController(
            UserService service) {

        this.service = service;
    }

    @PostMapping
    @PreAuthorize(
            "hasAuthority('USER_CREATE')"
    )
    public ResponseEntity<UserResponse> createUser(
            @Valid
            @RequestBody
            CreateUserRequest request, Authentication authentication) {

        
        return ResponseEntity.ok(
                service.createUser(
                        request)
        );
    }
   
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_VIEW')")
    public ResponseEntity<UserResponse>
    getUser(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                service.getUser(id)
        );
    }
    
    @GetMapping
    @PreAuthorize("hasAuthority('USER_VIEW')")
    public ResponseEntity<Page<UserResponse>>
    searchUsers(

            @RequestParam(
                    required = false,
                    defaultValue = ""
            )
            String username,

            @PageableDefault(
                    size = 20
            )
            Pageable pageable) {

        return ResponseEntity.ok(
                service.searchUsers(
                        username,
                        pageable
                )
        );
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ResponseEntity<UserResponse>
    updateUser(

            @PathVariable UUID id,

            @Valid
            @RequestBody
            UpdateUserRequest request) {

        return ResponseEntity.ok(
                service.updateUser(
                        id,
                        request
                )
        );
    }
    
    @PatchMapping("/{id}/disable")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ResponseEntity<Void>
    disableUser(
            @PathVariable UUID id) {

        service.disableUser(id);

        return ResponseEntity.noContent()
                .build();
    }
  
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ResponseEntity<Void>
    activateUser(
            @PathVariable UUID id) {

    	service.activateUser(id);
    	
    	return ResponseEntity.noContent()
                .build();
    }
    
    @PostMapping("/{userId}/roles")
    @PreAuthorize(
            "hasAuthority('USER_UPDATE')"
    )
    public ResponseEntity<Void> assignRole(
            @PathVariable UUID userId,
            @RequestBody AssignRoleRequest request) {

    	System.out.println("Assigning role " + request.roleId() + " to user " + userId);
    	
        service.assignRole(
                userId,
                request.roleId()
        );

        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping(
            "/{userId}/roles/{roleId}"
    )
    @PreAuthorize(
            "hasAuthority('USER_UPDATE')"
    )
    public ResponseEntity<Void> removeRole(
            @PathVariable UUID userId,
            @PathVariable UUID roleId) {

        service.removeRole(
                userId,
                roleId
        );

        return ResponseEntity.noContent()
                .build();
    }
}