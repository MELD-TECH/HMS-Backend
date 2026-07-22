package com.hms.api.test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hms.api.HmsApplication;
import com.hms.identity.entity.User;
import com.hms.identity.repository.UserRepository;
import com.hms.notification.mfa.entity.OtpCode;
import com.hms.notification.mfa.enums.MfaType;
import com.hms.notification.mfa.enums.OtpStatus;
import com.hms.notification.mfa.repository.OtpRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(
        classes = HmsApplication.class
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseIntegrationTest
        extends PostgreSQLContainerConfig {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;
    
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OtpRepository otpRepository;
	
    
	protected String obtainAdminToken()
	        throws Exception {

	    return authenticate(
	            "admin",
	            "password");
	}
  
	protected String obtainToken(String username)
	        throws Exception {

	    return authenticate(
	            username,
	            "password");
	}  
	
	protected JsonNode login(
	        String username,
	        String password)
	        throws Exception {

	    String response =

	            mockMvc.perform(

	                    post("/auth/login")

	                            .contentType(MediaType.APPLICATION_JSON)

	                            .content("""
	                            {
	                                "username":"%s",
	                                "password":"%s"
	                            }
	                            """.formatted(
	                                    username,
	                                    password)))

	                    .andExpect(status().isOk())

	                    .andReturn()

	                    .getResponse()

	                    .getContentAsString();

	    return objectMapper.readTree(response);
	}
	
	protected String obtainSecurityAdminToken()
	        throws Exception {

	    return authenticate(
	            "security-admin",
	            "password");
	}
    
    protected String authenticate(
            String username,
            String password)
            throws Exception {

        JsonNode tokens =
                authenticateAndReturnTokens(
                        username,
                        password);

        return tokens.get("accessToken")
                .asText();
    }
    
    protected JsonNode authenticateAndReturnTokens(
            String username,
            String password)
            throws Exception {

        JsonNode login =
                login(
                        username,
                        password);

        /*
         * Login completed directly.
         */
        if (!login.get("mfaRequired").asBoolean()) {

            return login;
        }

        return completeAuthentication(
                login,
                username);
    }
    
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected JsonNode loginResponse()
            throws Exception {

        String username = "admin";
        String password = "password";
       
        return objectMapper.readTree(login(username, password).asText());

    }
    
    private JsonNode completeAuthentication(
            JsonNode login,
            String username)
            throws Exception {

        String challengeToken =
                login.get("challengeToken")
                        .asText();

        User user =
                userRepository

                        .findByUsername(username)

                        .orElseThrow();

        MfaType type =
                login.hasNonNull("mfaType")

                        ? MfaType.valueOf(
                                login.get("mfaType")
                                        .asText())

                        : MfaType.EMAIL;

        OtpCode otp =

                otpRepository

                        .findTopByUserIdAndTypeAndStatusOrderByCreatedAtDesc(

                                user.getId(),

                                type,

                                OtpStatus.ACTIVE)

                        .orElseThrow();

        String response =

                mockMvc.perform(

                        post("/auth/mfa/complete")

                                .contentType(MediaType.APPLICATION_JSON)

                                .content("""
                                {
                                    "challengeToken":"%s",
                                    "otp":"%s"
                                }
                                """.formatted(
                                        challengeToken,
                                        otp.getCode())))

                        .andExpect(status().isOk())

                        .andReturn()

                        .getResponse()

                        .getContentAsString();

        return objectMapper.readTree(response);
    }
    
    protected JsonNode authenticateAdmin()
            throws Exception {

        return authenticateAndReturnTokens(
                "admin",
                "password");
    }
   
    protected JsonNode authenticateSecurityAdmin()
            throws Exception {

        return authenticateAndReturnTokens(
                "security-admin",
                "password");
    }
    
    protected String asJson(Object object)
            throws Exception {

        return objectMapper.writeValueAsString(object);
    }
    
    protected ResultActions performAuthenticatedRequest(
            HttpMethod method,
            String uri,
            Object request)
            throws Exception {

        return mockMvc.perform(

                MockMvcRequestBuilders.request(
                        method,
                        uri)

                        .header(
                                HttpHeaders.AUTHORIZATION,
                                bearerToken())

                        .contentType(
                                MediaType.APPLICATION_JSON)

                        .content(
                                asJson(request)));
    }
    
    protected ResultActions performAuthenticatedRequest(
            HttpMethod method,
            String uri)
            throws Exception {

        String token = bearerToken();

        return mockMvc.perform(
                MockMvcRequestBuilders.request(method, uri)
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                token));
    }
    
    protected ResultActions performWithoutAuthentication(
            HttpMethod method,
            String uri,
            Object request)
            throws Exception {

        return mockMvc.perform(
                MockMvcRequestBuilders.request(method, uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                        		asJson(request)
                                ));
    }
    
    protected ResultActions performAsSecurityAdmin(
            HttpMethod method,
            String uri,
            Object request)
            throws Exception {

        String token = bearerSecurityAdminToken();

        return mockMvc.perform(
                MockMvcRequestBuilders.request(method, uri)
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                        		asJson(request)
                                        ));
    }
    
    protected <T, ID> T reload(
            JpaRepository<T, ID> repository,
            ID id) {

        return repository.findById(id)

                .orElseThrow();
    }
    
	protected String bearerToken() throws Exception{

	    return "Bearer " + obtainAdminToken();
	}
	
	protected String bearerSecurityAdminToken() throws Exception{

	    return "Bearer " + obtainSecurityAdminToken();
	}
	
  
	protected String obtainDoctorToken()
	        throws Exception {

	    return authenticate(
	            "doctor",
	            "password");
	}

	protected String bearerToken(String username)
	        throws Exception {

	    return "Bearer " + obtainToken(username);
	}
	
}