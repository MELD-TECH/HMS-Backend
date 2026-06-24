package com.hms.identity.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hms.common.exception.BusinessException;
import com.hms.common.exception.ResourceNotFoundException;
import com.hms.identity.dto.ChangePasswordRequest;
import com.hms.identity.dto.ProfileResponse;
import com.hms.identity.dto.UpdateProfileRequest;
import com.hms.identity.entity.Permission;
import com.hms.identity.entity.Role;
import com.hms.identity.entity.User;
import com.hms.identity.repository.UserRepository;



@Service
public class ProfileService {

	private final UserRepository repository;
	
	private final PasswordEncoder passwordEncoder;
	
	public ProfileService(UserRepository repository, PasswordEncoder passwordEncoder) {
		super();
		this.repository = repository;
		this.passwordEncoder = passwordEncoder;
	}


	@Transactional(readOnly = true)
	public ProfileResponse me(
	        String username) {

	    User user =
	            repository.findByUsernameWithRolesAndPermissions(
	                    username
	            )
	            .orElseThrow(
	                    () -> new ResourceNotFoundException(
	                            "User not found"
	                    )
	            );

	    return toProfileResponse(user);
	}
	
	@Transactional
	public ProfileResponse updateProfile(
	        String username,
	        UpdateProfileRequest request) {

	    User user =
	            repository.findByUsernameWithRolesAndPermissions(
	                    username
	            )
	            .orElseThrow(
	                    () -> new ResourceNotFoundException(
	                            "User not found"
	                    )
	            );

	    user.setFirstName(request.firstName());
	    user.setLastName(request.lastName());
	    user.setEmail(request.email());

	    return toProfileResponse(
	            repository.save(user)
	    );
	}
	
	
	@Transactional
	public void changePassword(

	        String username,

	        ChangePasswordRequest request) {

	    User user =
	            repository.findByUsername(
	                    username
	            )
	            .orElseThrow(
	                    () ->
	                            new ResourceNotFoundException(
	                                    "User not found"
	                            )
	            );

	    if (!passwordEncoder.matches(
	            request.currentPassword(),
	            user.getPasswordHash())) {

	        throw new BusinessException(
	                "Current password is invalid"
	        );
	    }

	    user.setPasswordHash(
	            passwordEncoder.encode(
	                    request.newPassword()
	            )
	    );

	    repository.save(user);
	}
	
	private ProfileResponse toProfileResponse(
	        User user) {

	    Set<String> roles =
	            user.getRoles()
	                    .stream()
	                    .map(Role::getName)
	                    .collect(Collectors.toSet());

	    Set<String> permissions =
	            user.getRoles()
	                    .stream()
	                    .flatMap(
	                            role ->
	                                    role.getPermissions()
	                                            .stream()
	                    )
	                    .map(Permission::getCode)
	                    .collect(Collectors.toSet());

	    return new ProfileResponse(
	            user.getId(),
	            user.getUsername(),
	            user.getEmail(),
	            user.getFirstName(),
	            user.getLastName(),
	            roles,
	            permissions
	    );
	}
}
