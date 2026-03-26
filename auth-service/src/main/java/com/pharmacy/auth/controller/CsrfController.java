package com.pharmacy.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Exposes a CSRF token endpoint so clients can fetch a token before
 * performing state-changing operations that require CSRF protection.
 * Usage: GET /api/auth/csrf  → returns { "token": "...", "headerName": "X-XSRF-TOKEN" }
 */
@RestController
@RequestMapping("/api/auth")
public class CsrfController {

    @GetMapping("/csrf")
    public ResponseEntity<Map<String, String>> getCsrfToken(HttpServletRequest request) {
        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (token == null) {
            return ResponseEntity.ok(Map.of("message", "CSRF not required for this session"));
        }
        return ResponseEntity.ok(Map.of(
                "token", token.getToken(),
                "headerName", token.getHeaderName(),
                "parameterName", token.getParameterName()
        ));
    }
}
