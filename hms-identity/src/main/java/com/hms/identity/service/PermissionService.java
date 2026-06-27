package com.hms.identity.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hms.common.exception.BusinessException;
import com.hms.common.exception.ResourceNotFoundException;
import com.hms.identity.audit.dto.AuditRequest;
import com.hms.identity.audit.enums.AuditAction;
import com.hms.identity.audit.enums.AuditModule;
import com.hms.identity.audit.service.AuditService;
import com.hms.identity.audit.util.AuditContext;
import com.hms.identity.audit.util.JsonDiffUtil;
import com.hms.identity.dto.CreatePermissionRequest;
import com.hms.identity.dto.PermissionResponse;
import com.hms.identity.dto.UpdatePermissionRequest;
import com.hms.identity.entity.Permission;
import com.hms.identity.repository.PermissionRepository;
import com.hms.security.util.SecurityUtils;

import jakarta.transaction.Transactional;


@Service
public class PermissionService {

    private final PermissionRepository repository;
    private final AuditService auditService;
    private final JsonDiffUtil 	jsonUtil;

    
    public PermissionService(
    		PermissionRepository repository,
    		            AuditService auditService,
    		            JsonDiffUtil jsonUtil) {

        this.repository = repository;
        this.auditService = auditService;
        this.jsonUtil = jsonUtil;
    }

    public PermissionResponse createPermission(
            CreatePermissionRequest request) {

        if (repository.existsByCode(
                request.code())) {

            throw new BusinessException(
                    "Permission already exists");
        }

        Permission perm =
                Permission.builder()
                        .code(request.code())
                        .description(
                                request.description())
                        .build();

        Permission saved =
                repository.save(perm);
        
        String after = jsonUtil.toJson(saved);

        auditService.log(
                AuditRequest.builder()
                        .username(SecurityUtils.getCurrentUsername())
                        .action(AuditAction.PERMISSION_CREATED.name())
                        .module(AuditModule.IDENTITY.name())
                        .entity("ROLE")
                        .entityId(saved.getId().toString())
                        .beforeJson(null)
                        .afterJson(after)
                        .details("Created Permission")
                        .ipAddress(AuditContext.getIpAddress())
                        .userAgent(AuditContext.getUserAgent())
                        .build());
        
        
        return new PermissionResponse(
                saved.getId(),
                saved.getCode(),
                saved.getDescription()
        );
    }
   
    public List<PermissionResponse>
    getPermissions() {

        return repository.findAll()
                .stream()
				.map(perm -> new PermissionResponse(perm.getId(), perm.getCode(), perm.getDescription()))
                .toList();
    }
    
    public PermissionResponse getPermission(
            UUID id) {

        Permission permission =
                repository.findById(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Permission not found"
                                        )
                        );

        return new PermissionResponse(
                permission.getId(),
                permission.getCode(),
                permission.getDescription()
        );
    }
    
    @Transactional
    public PermissionResponse updatePermission(
            UUID id,
            UpdatePermissionRequest request) {

        Permission permission =
                repository.findById(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Permission not found"
                                        )
                        );

        String before = jsonUtil.toJson(permission);
        
        permission.setDescription(
                request.description()
        );

        Permission updated =
                repository.save(permission);
        
        String after = jsonUtil.toJson(updated);

        auditService.log(
                AuditRequest.builder()
                        .username(SecurityUtils.getCurrentUsername())
                        .action(AuditAction.PERMISSION_UPDATED.name())
                        .module(AuditModule.IDENTITY.name())
                        .entity("ROLE")
                        .entityId(updated.getId().toString())
                        .beforeJson(before)
                        .afterJson(after)
                        .details("Updated Permission")
                        .ipAddress(AuditContext.getIpAddress())
                        .userAgent(AuditContext.getUserAgent())
                        .build());
        
        return new PermissionResponse(
                updated.getId(),
                updated.getCode(),
                updated.getDescription()
        );
    }
}
