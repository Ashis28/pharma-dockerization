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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepo, PasswordEncoder encoder, JwtService jwtService) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    public String signup(SignupRequest req) {
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered: " + req.getEmail());
        }
        Set<Role> roles;
        try {
            roles = (req.getRoles() == null || req.getRoles().isEmpty())
                    ? Set.of(Role.CUSTOMER)
                    : req.getRoles().stream().map(Role::valueOf).collect(Collectors.toSet());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role specified. Valid roles: CUSTOMER, ADMIN, PHARMACIST");
        }

        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setRoles(roles);
        userRepo.save(user);
        return "User registered successfully";
    }

    public LoginResponse login(LoginRequest req) {
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + req.getEmail()));
        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        String token = jwtService.generateToken(user.getEmail(), user.getRoles());
        Set<String> roles = user.getRoles().stream().map(Role::name).collect(Collectors.toSet());
        return new LoginResponse(token, user.getName(), roles);
    }
}
