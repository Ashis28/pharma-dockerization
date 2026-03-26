package com.pharmacy.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmacy.auth.dto.LoginRequest;
import com.pharmacy.auth.dto.LoginResponse;
import com.pharmacy.auth.dto.SignupRequest;
import com.pharmacy.auth.exception.GlobalExceptionHandler;
import com.pharmacy.auth.exception.InvalidCredentialsException;
import com.pharmacy.auth.exception.UserAlreadyExistsException;
import com.pharmacy.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@Import({GlobalExceptionHandler.class, com.pharmacy.auth.security.SecurityConfig.class})
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean AuthService authService;

    @Test
    void signup_returns201OnSuccess() throws Exception {
        SignupRequest req = new SignupRequest();
        req.setName("Test User");
        req.setEmail("test@example.com");
        req.setPassword("password123");

        when(authService.signup(any())).thenReturn("User registered successfully");

        mockMvc.perform(post("/api/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    void signup_returns409WhenEmailExists() throws Exception {
        SignupRequest req = new SignupRequest();
        req.setName("Test");
        req.setEmail("test@example.com");
        req.setPassword("password123");

        when(authService.signup(any())).thenThrow(new UserAlreadyExistsException("Email already registered: test@example.com"));

        mockMvc.perform(post("/api/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already registered: test@example.com"));
    }

    @Test
    void signup_returns400WhenInvalidEmail() throws Exception {
        SignupRequest req = new SignupRequest();
        req.setName("Test");
        req.setEmail("not-an-email");
        req.setPassword("password123");

        mockMvc.perform(post("/api/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.email").exists());
    }

    @Test
    void login_returns200WithToken() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("test@example.com");
        req.setPassword("password123");

        LoginResponse response = new LoginResponse("jwt-token", "Test User", Set.of("CUSTOMER"));
        when(authService.login(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void login_returns401WhenInvalidCredentials() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("test@example.com");
        req.setPassword("wrongpass");

        when(authService.login(any())).thenThrow(new InvalidCredentialsException("Invalid email or password"));

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void login_returns400WhenBlankEmail() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("");
        req.setPassword("password");

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
}
