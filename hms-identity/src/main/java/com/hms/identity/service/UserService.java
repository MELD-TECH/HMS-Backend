package com.hms.identity.service;

import com.hms.common.exception.BusinessException;
import com.hms.common.exception.ResourceNotFoundException;
import com.hms.identity.dto.*;
import com.hms.identity.entity.Role;
import com.hms.identity.entity.User;
import com.hms.identity.enums.UserStatus;
import com.hms.identity.mapper.UserMapper;
import com.hms.identity.repository.RoleRepository;
import com.hms.identity.repository.UserRepository;

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


    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository repository,
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository) {

        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;

    }

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
    }
}