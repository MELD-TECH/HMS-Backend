package com.hms.identity.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hms.common.exception.BusinessException;
import com.hms.identity.dto.CreatePermissionRequest;
import com.hms.identity.dto.PermissionResponse;
import com.hms.identity.entity.Permission;
import com.hms.identity.repository.PermissionRepository;


@Service
public class PermissionService {

    private final PermissionRepository repository;

    public PermissionService(
    		PermissionRepository repository) {

        this.repository = repository;
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
}
