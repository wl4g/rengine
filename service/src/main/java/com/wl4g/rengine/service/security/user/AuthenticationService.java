package com.wl4g.rengine.service.security.user;

public class AuthenticationService {
    private static final String USERNAME = "username";

    public void authenticate(String username, String password) {
        if (USERNAME.equals(username)) {
            // authenticate
        }
    }

    // ... other methods using USERNAME constant
}
