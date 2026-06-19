package com.hms.identity.mapper;

import java.util.List;

import com.hms.identity.dto.UserResponse;
import com.hms.identity.entity.User;

public class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(
            User user) {

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getStatus().name()
        );
    }
    
    public static List<UserResponse> toResponseList(
            List<User> users) {

        return users.stream()
                .map(UserMapper::toResponse)
                .toList();
    }
}