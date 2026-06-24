package com.hms.api.identity.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hms.api.test.BaseIntegrationTest;
import com.hms.identity.dto.UserResponse;
import com.hms.identity.entity.User;
import com.hms.identity.enums.UserStatus;
import com.hms.identity.repository.UserRepository;
import com.hms.identity.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService service;

    @Test
    void shouldReturnUserWhenFound() {

    	UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        user.setUsername("doctor1");
        user.setStatus(UserStatus.ACTIVE);;

        when(repository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        UserResponse response =
                service.getUser(user.getId());

        assertThat(response.username())
                .isEqualTo("doctor1");
    }
}