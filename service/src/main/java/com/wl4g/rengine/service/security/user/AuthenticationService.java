package com.wl4g.rengine.service.security.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class AuthenticationService implements UserDetailsService {
    private static final String USERNAME_FIELD = "username";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Use USERNAME_FIELD constant instead of literal "username"
        String field = USERNAME_FIELD;
        // ... rest of method
        return null;
    }

    // Other methods using USERNAME_FIELD constant
}
