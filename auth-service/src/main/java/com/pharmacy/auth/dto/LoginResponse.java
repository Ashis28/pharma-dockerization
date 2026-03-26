package com.pharmacy.auth.dto;

import java.util.Set;

public class LoginResponse {
    private String token;
    private String name;
    private Set<String> roles;

    public LoginResponse(String token, String name, Set<String> roles) {
        this.token = token;
        this.name = name;
        this.roles = roles;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}
