package com.pharmacy.auth.service;

import com.pharmacy.auth.dto.LoginRequest;
import com.pharmacy.auth.dto.LoginResponse;
import com.pharmacy.auth.dto.SignupRequest;
import com.pharmacy.auth.exception.InvalidCredentialsException;
import com.pharmacy.auth.exception.UserAlreadyExistsException;
import com.pharmacy.auth.exception.UserNotFoundException;
import com.pharmacy.auth.model.Role;
import com.pharmacy.auth.model.User;
import com.pharmacy.auth.repository.UserRepository;
import com.pharmacy.auth.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepo;
    @Mock PasswordEncoder encoder;
    @Mock JwtService jwtService;

    @InjectMocks AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRoles(Set.of(Role.CUSTOMER));
    }

    // --- signup tests ---

    @Test
    void signup_success() {
        SignupRequest req = new SignupRequest();
        req.setName("Test User");
        req.setEmail("new@example.com");
        req.setPassword("password123");

        when(userRepo.existsByEmail("new@example.com")).thenReturn(false);
        when(encoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepo.save(any(User.class))).thenReturn(testUser);

        String result = authService.signup(req);

        assertThat(result).isEqualTo("User registered successfully");
        verify(userRepo).save(any(User.class));
    }

    @Test
    void signup_throwsWhenEmailAlreadyExists() {
        SignupRequest req = new SignupRequest();
        req.setEmail("test@example.com");
        req.setName("Test");
        req.setPassword("pass");

        when(userRepo.existsByEmail("test@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.signup(req))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("test@example.com");
    }

    @Test
    void signup_withInvalidRole_throwsIllegalArgument() {
        SignupRequest req = new SignupRequest();
        req.setEmail("new@example.com");
        req.setName("Test");
        req.setPassword("pass");
        req.setRoles(Set.of("INVALID_ROLE"));

        when(userRepo.existsByEmail("new@example.com")).thenReturn(false);

        assertThatThrownBy(() -> authService.signup(req))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void signup_withDefaultCustomerRole_whenRolesNull() {
        SignupRequest req = new SignupRequest();
        req.setName("Test");
        req.setEmail("new@example.com");
        req.setPassword("pass");
        req.setRoles(null);

        when(userRepo.existsByEmail("new@example.com")).thenReturn(false);
        when(encoder.encode(anyString())).thenReturn("encoded");
        when(userRepo.save(any(User.class))).thenReturn(testUser);

        String result = authService.signup(req);
        assertThat(result).isEqualTo("User registered successfully");
    }

    // --- login tests ---

    @Test
    void login_success() {
        LoginRequest req = new LoginRequest();
        req.setEmail("test@example.com");
        req.setPassword("password123");

        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(encoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(eq("test@example.com"), anySet())).thenReturn("jwt-token");

        LoginResponse response = authService.login(req);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getName()).isEqualTo("Test User");
    }

    @Test
    void login_throwsWhenUserNotFound() {
        LoginRequest req = new LoginRequest();
        req.setEmail("unknown@example.com");
        req.setPassword("pass");

        when(userRepo.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("unknown@example.com");
    }

    @Test
    void login_throwsWhenPasswordWrong() {
        LoginRequest req = new LoginRequest();
        req.setEmail("test@example.com");
        req.setPassword("wrongPassword");

        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(encoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
