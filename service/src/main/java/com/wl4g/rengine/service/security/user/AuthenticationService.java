package com.wl4g.rengine.service.security.user;

import com.wl4g.rengine.common.entity.sys.User;
import com.wl4g.rengine.common.exception.RengineException;
import com.wl4g.rengine.service.security.user.AuthenticationService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private static final String USERNAME_FIELD = "username";
    private static final String PASSWORD_FIELD = "password";

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    public Authentication authenticate(String username, String password) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    public void validateCredentials(String username, String password) {
        if (username == null || username.isEmpty()) {
            throw new RengineException(USERNAME_FIELD + " cannot be empty");
        }
        if (password == null || password.isEmpty()) {
            throw new RengineException(PASSWORD_FIELD + " cannot be empty");
        }
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean matchesPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
