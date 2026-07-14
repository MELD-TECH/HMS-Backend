package com.hms.api.identity.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import com.fasterxml.jackson.databind.JsonNode;
import com.hms.api.test.BaseIntegrationTest;
import com.hms.identity.password.entity.PasswordResetToken;
import com.hms.identity.password.repository.PasswordResetTokenRepository;


@ActiveProfiles("test")
@Sql(
    scripts = {
        "/db/testdata/001_cleanup.sql",
        "/db/testdata/002_admin_user.sql",
        "/db/testdata/003_roles.sql",
        "/db/testdata/004_role_permissions.sql",
        "/db/testdata/005_password_history.sql"
    },
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class PasswordResetIntegrationTest
        extends BaseIntegrationTest {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;


    
    @Test
    void shouldGenerateResetToken()
            throws Exception {

        mockMvc.perform(

                post("/auth/forgot-password")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content("""
                        {
                            "email":"admin@hms.com"
                        }
                        """))

                .andExpect(status().isNoContent());

        assertEquals(
                1,
                tokenRepository.count());

        PasswordResetToken token =
                tokenRepository.findAll()
                        .getFirst();

        assertFalse(token.isUsed());

        assertTrue(
                token.getExpiresAt()
                        .isAfter(LocalDateTime.now()));
    }
    
    @Test
    void shouldIgnoreUnknownEmail()
            throws Exception {

        mockMvc.perform(

                post("/auth/forgot-password")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content("""
                        {
                            "email":"unknown@hms.com"
                        }
                        """))

                .andExpect(status().isNoContent());

        assertEquals(
                0,
                tokenRepository.count());
    }
    
    @Test
    void shouldExpirePreviousResetToken()
            throws Exception {

        mockMvc.perform(
                post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "email":"admin@hms.com"
                        }
                        """));

        mockMvc.perform(
                post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "email":"admin@hms.com"
                        }
                        """));

        List<PasswordResetToken> tokens =
                tokenRepository.findAll();

        assertEquals(
                2,
                tokens.size());

        long active =

                tokens.stream()

                        .filter(t -> !t.isUsed())

                        .count();

        assertEquals(
                1,
                active);
    }
    
    @Test
    void shouldResetPassword()
            throws Exception {

        /*
         Generate token
         */

        mockMvc.perform(

                post("/auth/forgot-password")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content("""
                        {
                           "email":"admin@hms.com"
                        }
                        """))

                .andExpect(status().isNoContent());

        PasswordResetToken token =

                tokenRepository.findAll()

                        .getFirst();

        mockMvc.perform(

                post("/auth/reset-password")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content("""
                        {
                           "token":"%s",
                           "newPassword":"Password@12345",
                           "confirmPassword":"Password@12345"
                        }
                        """.formatted(
                                token.getToken())))

                .andExpect(status().isNoContent());

        /*
         Login using new password
         */

        mockMvc.perform(

                post("/auth/login")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content("""
                        {
                            "username":"admin",
                            "password":"Password@12345"
                        }
                        """))

                .andExpect(status().isOk());
    }
    
    @Test
    void shouldRejectInvalidToken()
            throws Exception {

        mockMvc.perform(

                post("/auth/reset-password")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content("""
                        {
                           "token":"INVALID",
                           "newPassword":"Password@12345",
                           "confirmPassword":"Password@12345"
                        }
                        """))

                .andExpect(status().isBadRequest());
    }
    
    @Test
    void shouldRejectUsedToken()
            throws Exception {

        mockMvc.perform(

                post("/auth/forgot-password")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content("""
                        {
                           "email":"admin@hms.com"
                        }
                        """));

        PasswordResetToken token =
                tokenRepository.findAll().getFirst();

        token.setUsed(true);

        tokenRepository.save(token);

        mockMvc.perform(

                post("/auth/reset-password")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content("""
                        {
                           "token":"%s",
                           "newPassword":"Password@12345",
                           "confirmPassword":"Password@12345"
                        }
                        """.formatted(
                                token.getToken())))

                .andExpect(status().isBadRequest());
    }
    
    @Test
    void shouldRejectExpiredToken()
            throws Exception {

        mockMvc.perform(

                post("/auth/forgot-password")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content("""
                        {
                           "email":"admin@hms.com"
                        }
                        """));

        PasswordResetToken token =
                tokenRepository.findAll().getFirst();

        token.setExpiresAt(
                LocalDateTime.now()
                        .minusMinutes(1));

        tokenRepository.save(token);

        mockMvc.perform(

                post("/auth/reset-password")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content("""
                        {
                           "token":"%s",
                           "newPassword":"Password@12345",
                           "confirmPassword":"Password@12345"
                        }
                        """.formatted(
                                token.getToken())))

                .andExpect(status().isBadRequest());
    }
    
    @Test
    void shouldRejectPasswordMismatch()
            throws Exception {

        mockMvc.perform(

                post("/auth/forgot-password")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content("""
                        {
                           "email":"admin@hms.com"
                        }
                        """));

        PasswordResetToken token =
                tokenRepository.findAll().getFirst();

        mockMvc.perform(

                post("/auth/reset-password")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content("""
                        {
                           "token":"%s",
                           "newPassword":"Password@12345",
                           "confirmPassword":"Password@99999"
                        }
                        """.formatted(
                                token.getToken())))

                .andExpect(status().isBadRequest());
    }
    
    @Test
    void shouldRejectPasswordReuse()
            throws Exception {

        mockMvc.perform(

                post("/auth/forgot-password")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content("""
                        {
                           "email":"admin@hms.com"
                        }
                        """));

        PasswordResetToken token =
                tokenRepository.findAll().getFirst();

        mockMvc.perform(

                post("/auth/reset-password")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content("""
                        {
                           "token":"%s",
                           "newPassword":"password",
                           "confirmPassword":"password"
                        }
                        """.formatted(
                                token.getToken())))

                .andExpect(status().isBadRequest());
    }
    
    @Test
    void shouldRejectWeakPassword()
            throws Exception {

        mockMvc.perform(

                post("/auth/forgot-password")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content("""
                        {
                           "email":"admin@hms.com"
                        }
                        """));

        PasswordResetToken token =
                tokenRepository.findAll().getFirst();

        mockMvc.perform(

                post("/auth/reset-password")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content("""
                        {
                           "token":"%s",
                           "newPassword":"abc",
                           "confirmPassword":"abc"
                        }
                        """.formatted(
                                token.getToken())))

                .andExpect(status().isBadRequest());
    }
    
    @Test
    void shouldRevokeSessionsAfterReset()
            throws Exception {

        JsonNode auth = authenticateAndReturnTokens("admin", "password");

        String refreshToken =
                auth.get("refreshToken").asText();

        mockMvc.perform(

                post("/auth/forgot-password")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content("""
                        {
                           "email":"admin@hms.com"
                        }
                        """));

        PasswordResetToken token =
                tokenRepository.findAll().getFirst();

        mockMvc.perform(

                post("/auth/reset-password")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content("""
                        {
                           "token":"%s",
                           "newPassword":"Password@12345",
                           "confirmPassword":"Password@12345"
                        }
                        """.formatted(
                                token.getToken())))

                .andExpect(status().isNoContent());

        mockMvc.perform(

                post("/auth/refresh")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content("""
                        {
                           "refreshToken":"%s"
                        }
                        """.formatted(
                                refreshToken)))

                .andExpect(status().isUnauthorized());
    }
    
    @Test
    void shouldNotReuseResetToken()
            throws Exception {

        mockMvc.perform(

                post("/auth/forgot-password")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content("""
                        {
                           "email":"admin@hms.com"
                        }
                        """));

        PasswordResetToken token =
                tokenRepository.findAll().getFirst();

        String body = """
        {
           "token":"%s",
           "newPassword":"Password@12345",
           "confirmPassword":"Password@12345"
        }
        """.formatted(token.getToken());

        mockMvc.perform(
                post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());

        mockMvc.perform(
                post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
    
    
}
