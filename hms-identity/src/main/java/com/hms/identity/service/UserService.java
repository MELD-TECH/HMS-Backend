package com.hms.identity.service;

import com.hms.common.exception.BusinessException;
import com.hms.common.exception.ResourceNotFoundException;
import com.hms.identity.audit.annotation.Auditable;
import com.hms.identity.audit.event.AuditEventPublisher;
import com.hms.identity.audit.service.AuditService;
import com.hms.identity.dto.*;
import com.hms.identity.entity.Role;
import com.hms.identity.entity.User;
import com.hms.identity.enums.UserStatus;
import com.hms.identity.mapper.UserMapper;
import com.hms.identity.repository.RoleRepository;
import com.hms.identity.repository.UserRepository;
import com.hms.security.util.SecurityUtils;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final AuditService auditService;


    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository repository,
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository,
            AuditService auditService) {

        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.auditService = auditService;
        

    }

    @Auditable(
            action = "USER_CREATED",
            entity = "USER"
    )
    public UserResponse createUser(
            CreateUserRequest request) {

        if (repository.existsByUsername(
                request.username())) {

            throw new BusinessException(
                    "Username already exists"
            );
        }

        User user = User.builder()
                .username(
                        request.username())
                .email(
                        request.email())
                .passwordHash(
                        passwordEncoder.encode(
                                request.password()))
                .firstName(
                        request.firstName())
                .lastName(
                        request.lastName())
                .status(
                        UserStatus.ACTIVE)
                .build();

        User saved =
                repository.save(user);
        
        auditService.log(
                SecurityUtils.getCurrentUsername(),
                "USER_CREATED",
                "USER",
                saved.getId().toString(),
                "Created user " + saved.getUsername(),
                null
        );

        return UserMapper.toResponse(
                saved);
    }
    
    public UserResponse getUser(UUID id) {

        User user = repository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "User not found"
                        )
                );

        return UserMapper.toResponse(user);
    }
    
    public Page<UserResponse> searchUsers(
            String username,
            Pageable pageable) {

        return repository
                .findByUsernameContainingIgnoreCase(
                        username,
                        pageable
                )
                .map(UserMapper::toResponse);
    }
    
    @Transactional
    public UserResponse updateUser(
            UUID id,
            UpdateUserRequest request) {

        User user = repository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "User not found"
                        )
                );

        user.setFirstName(
                request.firstName());

        user.setLastName(
                request.lastName());

        user.setEmail(
                request.email());

        User updated =
                repository.save(user);

        auditService.log(
                SecurityUtils.getCurrentUsername(),
                "USER_UPDATED",
                "USER",
                updated.getId().toString(),
                "Updated user " + updated.getUsername(),
                null
        );
        
        return UserMapper.toResponse(updated);
    }
    
    @Transactional
    public void disableUser(UUID id) {

        User user = repository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "User not found"
                        )
                );

        user.setStatus(
                UserStatus.DISABLED
        );

        auditService.log(
                SecurityUtils.getCurrentUsername(),
                "USER_DISABLED",
                "USER",
                user.getId().toString(),
                "Disabled user " + user.getUsername(),
                null
        );
        
        repository.save(user);
    }
   
    @Transactional
    public void activateUser(UUID id) {

        User user = repository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "User not found"
                        )
                );

        user.setStatus(UserStatus.ACTIVE);

        auditService.log(
                SecurityUtils.getCurrentUsername(),
                "USER_ACTIVATED",
                "USER",
                user.getId().toString(),
                "Activated user " + user.getUsername(),
                null
        );
        
        repository.save(user);
    }
   
    @Transactional
    public void assignRole(
            UUID userId,
            UUID roleId) {

        User user =
        		repository.findById(userId)
                .orElseThrow(
                		() -> new ResourceNotFoundException(
                        "User not found"
                ));

        Role role =
                roleRepository.findById(roleId)
                .orElseThrow(
                		() -> new ResourceNotFoundException(
                        "Role not found"
                ));

        user.getRoles().add(role);
        repository.save(user);
        
        auditService.log(
                SecurityUtils.getCurrentUsername(),
                "ROLE_ASSIGNED",
                "USER",
                user.getId().toString(),
                "Assigned role " + role.getName(),
                null
        );
       
    }
    
    @Transactional
    public void removeRole(
            UUID userId,
            UUID roleId) {

        User user =
        		repository.findById(userId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "User not found"
                                        )
                        );

        user.getRoles()
                .removeIf(
                        role ->
                                role.getId()
                                        .equals(roleId)
                );

        repository.save(user);
        
        auditService.log(
                SecurityUtils.getCurrentUsername(),
                "ROLE_REMOVED",
                "USER",
                user.getId().toString(),
                "Removed role " + roleId,
                null
        );
    }
}