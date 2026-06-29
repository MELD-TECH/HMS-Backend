package com.hms.api.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hms.api.HmsApplication;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


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
    
    protected String obtainAdminToken()
            throws Exception {

        String response =
                mockMvc.perform(
                        post("/auth/login")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                {
                                  "username":"admin",
                                  "password":"password"
                                }
                                """)
                )
                .andReturn()
                .getResponse()
                .getContentAsString();


        JsonNode json =
                objectMapper.readTree(response);

        if (!json.has("accessToken")) {

            throw new RuntimeException(
                    "Login failed: " + response
            );
        }

        return json.get("accessToken")
                .asText();
    }
    
    protected String login()
            throws Exception {

        return mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "username":"admin",
                          "password":"password"
                        }
                        """)
        )
        .andReturn()
        .getResponse()
        .getContentAsString();
    }
}