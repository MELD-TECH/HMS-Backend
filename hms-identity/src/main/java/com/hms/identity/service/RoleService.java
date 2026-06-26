package com.hms.identity.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hms.common.exception.BusinessException;
import com.hms.common.exception.ResourceNotFoundException;
import com.hms.identity.audit.service.AuditService;
import com.hms.identity.dto.CreateRoleRequest;
import com.hms.identity.dto.RoleResponse;
import com.hms.identity.dto.UpdateRoleRequest;
import com.hms.identity.entity.Permission;
import com.hms.identity.entity.Role;
import com.hms.identity.repository.PermissionRepository;
import com.hms.identity.repository.RoleRepository;
import com.hms.security.util.SecurityUtils;

import jakarta.transaction.Transactional;

@Service
public class RoleService {

    private final RoleRepository repository;
    private final PermissionRepository permissionRepository;
    private final AuditService auditService;

    public RoleService(
            RoleRepository repository,
            PermissionRepository permissionRepository
            , AuditService auditService) {

        this.repository = repository;
        this.permissionRepository = permissionRepository;
        this.auditService = auditService;
    }

    public RoleResponse createRole(
            CreateRoleRequest request) {

        if (repository.existsByName(
                request.name())) {

            throw new BusinessException(
                    "Role already exists");
        }

        Role role =
                Role.builder()
                        .name(request.name())
                        .description(
                                request.description())
                        .build();

        Role saved =
                repository.save(role);
        
        auditService.log(
                SecurityUtils.getCurrentUsername(),
                "ROLE_CREATED",
                "ROLE",
                saved.getId().toString(),
                saved.getName(),
                null
        );

        return new RoleResponse(
                saved.getId(),
                saved.getName(),
                saved.getDescription()
        );
    }
    
    @Transactional
    public RoleResponse updateRole(
            UUID roleId,
            UpdateRoleRequest request) {

        Role role =
                repository.findById(roleId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Role not found"
                                )
                        );

        role.setDescription(
                request.description()
        );

        Role updated =
                repository.save(role);

        auditService.log(
                SecurityUtils.getCurrentUsername(),
                "ROLE_UPDATED",
                "ROLE",
                updated.getId().toString(),
                updated.getName(),
                null
        );
        
        return new RoleResponse(
        		updated.getId(),
        		updated.getName(),
        		updated.getDescription()
        );
    }
    
    public RoleResponse getRole(
            UUID roleId) {

        Role role =
                repository.findById(roleId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Role not found"
                                )
                        );

        return new RoleResponse(
        		role.getId(),
        		role.getName(),
        		role.getDescription()
        );
    }
    
    public List<RoleResponse> getRoles() {

        return repository.findAll()
                .stream()
				.map(role -> new RoleResponse(role.getId(), role.getName(), role.getDescription()))
                .toList();
    }
    
    @Transactional
    public void assignPermission(
            UUID roleId,
            UUID permissionId) {

        Role role =
                repository.findById(roleId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Role not found"
                                        )
                        );

        Permission permission =
                permissionRepository
                        .findById(permissionId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Permission not found"
                                        )
                        );

        role.getPermissions()
                .add(permission);

        repository.save(role);
        
        auditService.log(
                SecurityUtils.getCurrentUsername(),
                "PERMISSION_ASSIGNED",
                "ROLE",
                role.getId().toString(),
                permission.getCode(),
                null
        );
    }
    
    @Transactional
    public void removePermission(
            UUID roleId,
            UUID permissionId) {

        Role role =
                repository.findById(roleId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Role not found"
                                        )
                        );

        role.getPermissions()
                .removeIf(
                        permission ->
                                permission.getId()
                                        .equals(permissionId)
                );

        repository.save(role);
        
        auditService.log(
                SecurityUtils.getCurrentUsername(),
                "PERMISSION_REMOVED",
                "ROLE",
                role.getId().toString(),
                permissionId.toString(),
                null
        );
    }
}