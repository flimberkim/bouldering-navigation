package com.boulderingnavigation.support;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.boulderingnavigation.dto.SignupRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

/**
 * Signs a fresh user up through the real {@code /api/auth/signup} endpoint and
 * returns its bearer token, so tests for authenticated endpoints get a real
 * token instead of mocking Spring Security — matching this project's
 * integration-test-everything-for-real philosophy (docs/ai/testing/backend-testing.md).
 */
public final class AuthTestSupport {

    private AuthTestSupport() {
    }

    public static String signupAndGetToken(MockMvc mockMvc, ObjectMapper objectMapper, String email, String nickname)
            throws Exception {
        String body = objectMapper.writeValueAsString(new SignupRequest(email, "password123", nickname));
        String response = mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("token").asText();
    }
}
