package com.wl4g.rengine.service.security.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements UserDetailsService {
    private static final String USERNAME_FIELD = "username";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // ... implementation using USERNAME_FIELD constant ...
        return null;
    }
}
