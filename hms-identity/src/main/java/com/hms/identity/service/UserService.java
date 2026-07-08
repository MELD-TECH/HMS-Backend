package com.hms.identity.service;

import com.hms.common.exception.BusinessException;
import com.hms.common.exception.ResourceNotFoundException;
import com.hms.identity.audit.annotation.Auditable;
import com.hms.identity.audit.dto.AuditRequest;
import com.hms.identity.audit.enums.AuditAction;
import com.hms.identity.audit.enums.AuditModule;
import com.hms.identity.audit.event.AuditEventPublisher;
import com.hms.identity.audit.service.AuditService;
import com.hms.identity.audit.util.AuditContext;
import com.hms.identity.audit.util.JsonDiffUtil;
import com.hms.identity.dto.*;
import com.hms.identity.entity.Role;
import com.hms.identity.entity.User;
import com.hms.identity.enums.UserStatus;
import com.hms.identity.mapper.UserMapper;
import com.hms.identity.repository.RoleRepository;
import com.hms.identity.repository.UserRepository;
import com.hms.identity.security.event.PasswordChangedEvent;
import com.hms.identity.security.event.RoleAssignedEvent;
import com.hms.identity.security.event.RoleRemovedEvent;
import com.hms.identity.security.event.UserActivatedEvent;
import com.hms.identity.security.event.UserCreatedEvent;
import com.hms.identity.security.event.UserDisabledEvent;
import com.hms.identity.security.event.UserUpdateEvent;
import com.hms.identity.security.publisher.SecurityEventPublisher;
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
    private final JsonDiffUtil jsonUtil;

    private final PasswordEncoder passwordEncoder;

    private final SecurityEventPublisher securityEventPublisher;
    
    public UserService(
            UserRepository repository,
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository,
            AuditService auditService,
            JsonDiffUtil jsonUtil,
            SecurityEventPublisher securityEventPublisher) {

        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.auditService = auditService;
        this.jsonUtil = jsonUtil;
        this.securityEventPublisher = securityEventPublisher;

    }

    @Auditable(
            action = "USER_CREATED",
            module = "IDENTITY",
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
       
	    securityEventPublisher.publish(

	            new UserCreatedEvent(

	                    user.getUsername(),

	                    user.getId().toString()));

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

        User updated = repository.save(user);
        
        securityEventPublisher.publish(new UserUpdateEvent(updated.getUsername(), updated.getId().toString()));
        
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


        user.setStatus(UserStatus.DISABLED);

        repository.save(user);


		securityEventPublisher.publish(new UserDisabledEvent(user.getUsername(), user.getId().toString()));
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
        
        repository.save(user);    


        securityEventPublisher.publish(new UserActivatedEvent(user.getUsername(), user.getId().toString()));

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
        
		securityEventPublisher.publish(new RoleAssignedEvent(user.getUsername(), user.getId().toString()));

	  
    }
       
     
    @Transactional
    public void removeRole(

            UUID userId,

            UUID roleId) {

        User user =
                repository.findById(userId)
                        .orElseThrow();

        Role role =
                roleRepository.findById(roleId)
                        .orElseThrow();

        user.getRoles().remove(role);

        securityEventPublisher.publish(

                new RoleRemovedEvent(

                        SecurityUtils.getCurrentUsername(),

                        user.getId().toString()));
    }
}