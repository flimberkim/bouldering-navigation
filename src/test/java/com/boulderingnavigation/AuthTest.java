package com.boulderingnavigation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.boulderingnavigation.support.AuthTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 회원가입하면_토큰과_함께_201을_반환한다() throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"climber@example.com\",\"password\":\"password123\",\"nickname\":\"클라이머\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.nickname").value("클라이머"));
    }

    @Test
    void 이미_가입된_이메일로_회원가입하면_409를_반환한다() throws Exception {
        AuthTestSupport.signupAndGetToken(mockMvc, objectMapper, "duplicate@example.com", "먼저가입");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"duplicate@example.com\",\"password\":\"password123\",\"nickname\":\"나중가입\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void 비밀번호가_8자_미만이면_400을_반환한다() throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"short@example.com\",\"password\":\"1234\",\"nickname\":\"짧은비번\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").exists());
    }

    @Test
    void 가입한_계정으로_로그인하면_토큰을_반환한다() throws Exception {
        AuthTestSupport.signupAndGetToken(mockMvc, objectMapper, "login@example.com", "로그인유저");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"login@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.nickname").value("로그인유저"));
    }

    @Test
    void 잘못된_비밀번호로_로그인하면_401을_반환한다() throws Exception {
        AuthTestSupport.signupAndGetToken(mockMvc, objectMapper, "wrongpw@example.com", "비번틀림");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"wrongpw@example.com\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 존재하지_않는_이메일로_로그인하면_401을_반환한다() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"nobody@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 유효한_토큰으로_내_정보를_조회할_수_있다() throws Exception {
        String token = AuthTestSupport.signupAndGetToken(mockMvc, objectMapper, "me@example.com", "내정보유저");

        mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("me@example.com"))
                .andExpect(jsonPath("$.nickname").value("내정보유저"));
    }

    @Test
    void 토큰_없이_내_정보를_조회하면_401을_반환한다() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }
}
