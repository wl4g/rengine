package com.wl4g.rengine.service.security.user;

public class AuthenticationService {
    private static final String USERNAME = "username";

    // Use USERNAME constant instead of literal "username"
    public void authenticate(String username) {
        if (USERNAME.equals(username)) {
            // logic
        }
    }
}
