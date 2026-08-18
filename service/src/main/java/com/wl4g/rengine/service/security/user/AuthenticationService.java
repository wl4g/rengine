package com.wl4g.rengine.service.security.user;

public class AuthenticationService {
    private static final String USERNAME_FIELD = "username";

    public boolean authenticate(String username, String password) {
        // Example usage
        if (username == null || password == null) {
            return false;
        }
        return USERNAME_FIELD.equals(username) && "password123".equals(password);
    }

    public String getUsernameField() {
        return USERNAME_FIELD;
    }
}
