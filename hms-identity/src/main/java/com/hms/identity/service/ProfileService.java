package com.hms.identity.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.hms.identity.dto.ProfileResponse;
import com.hms.identity.entity.Permission;
import com.hms.identity.entity.Role;
import com.hms.identity.entity.User;
import com.hms.identity.repository.UserRepository;

@Service
public class ProfileService {

	private final UserRepository repository;
	
	
	public ProfileService(UserRepository repository) {
		super();
		this.repository = repository;
	}


	public ProfileResponse me(
	        String username) {

	    User user =
	            repository
	                    .findByUsernameWithRolesAndPermissions(
	                            username
	                    )
	                    .orElseThrow();

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
