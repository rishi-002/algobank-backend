package com.bank.dto;

import jakarta.validation.constraints.*;

public class AuthDtos {

    public static class RegisterRequest {
        @NotBlank @Size(min = 3, max = 50)
        private String username;
        @NotBlank @Size(min = 6)
        private String password;
        @NotBlank @Size(max = 100)
        private String fullName;
        @NotBlank @Email
        private String email;
        private String phone;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }

    public static class LoginRequest {
        @NotBlank private String username;
        @NotBlank private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class AuthResponse {
        private String token;
        private String tokenType;
        private Long userId;
        private String username;
        private String fullName;
        private String role;

        public AuthResponse() {}
        public AuthResponse(String token, String tokenType, Long userId,
                            String username, String fullName, String role) {
            this.token = token; this.tokenType = tokenType; this.userId = userId;
            this.username = username; this.fullName = fullName; this.role = role;
        }
        public String getToken() { return token; }
        public String getTokenType() { return tokenType; }
        public Long getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getFullName() { return fullName; }
        public String getRole() { return role; }
    }
}
