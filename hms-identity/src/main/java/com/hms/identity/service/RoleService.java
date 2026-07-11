package com.hms.identity.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hms.audit.security.dto.AuditRequest;
import com.hms.audit.security.enums.AuditAction;
import com.hms.audit.security.enums.AuditModule;
import com.hms.audit.security.service.AuditService;
import com.hms.audit.security.util.AuditContext;
import com.hms.audit.security.util.JsonDiffUtil;
import com.hms.common.exception.BusinessException;
import com.hms.common.exception.ResourceNotFoundException;
import com.hms.events.security.PermissionAssignedEvent;
import com.hms.events.security.PermissionRemovedEvent;
import com.hms.events.security.RoleAssignedEvent;
import com.hms.events.security.publisher.SecurityEventPublisher;
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
    private JsonDiffUtil jsonUtil;
    private SecurityEventPublisher securityEventPublisher;

    public RoleService(
            RoleRepository repository,
            PermissionRepository permissionRepository,
            AuditService auditService,
            JsonDiffUtil jsonUtil,
            SecurityEventPublisher securityEventPublisher) {

        this.repository = repository;
        this.permissionRepository = permissionRepository;
        this.auditService = auditService;
        this.jsonUtil = jsonUtil;
        this.securityEventPublisher = securityEventPublisher;
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
        
        String after =
                jsonUtil.toJson(role);
        
        auditService.log(
                AuditRequest.builder()
                        .username(SecurityUtils.getCurrentUsername())
                        .action(AuditAction.ROLE_CREATED.name())
                        .module(AuditModule.IDENTITY.name())
                        .entity("ROLE")
                        .entityId(role.getId().toString())
                        .beforeJson(null)
                        .afterJson(after)
                        .details("Created role")
                        .ipAddress(AuditContext.getIpAddress())
                        .userAgent(AuditContext.getUserAgent())
                        .build());

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

        String before =
                jsonUtil.toJson(role);
        
        role.setDescription(
                request.description()
        );

        Role updated =
                repository.save(role);
        
        String after =
                jsonUtil.toJson(updated);

        auditService.log(
                AuditRequest.builder()
                        .username(SecurityUtils.getCurrentUsername())
                        .action(AuditAction.ROLE_UPDATED.name())
                        .module(AuditModule.IDENTITY.name())
                        .entity("ROLE")
                        .entityId(role.getId().toString())
                        .beforeJson(before)
                        .afterJson(after)
                        .details("Updated role")
                        .ipAddress(AuditContext.getIpAddress())
                        .userAgent(AuditContext.getUserAgent())
                        .build());
        
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
       
        securityEventPublisher.publish(

        	    new PermissionAssignedEvent(

        	            SecurityUtils.getCurrentUsername(),

        	            role.getId().toString())

        	);
    }
    
    @Transactional
    public void removePermission(

            UUID roleId,

            UUID permissionId) {

        Role role =
                repository.findById(roleId)
                        .orElseThrow();

        Permission permission =
                permissionRepository.findById(permissionId)
                        .orElseThrow();

        role.getPermissions().remove(permission);

        securityEventPublisher.publish(

                new PermissionRemovedEvent(

                        SecurityUtils.getCurrentUsername(),

                        role.getId().toString()));
    }
    
}