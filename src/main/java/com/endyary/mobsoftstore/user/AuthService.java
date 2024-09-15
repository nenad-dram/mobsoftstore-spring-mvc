package com.endyary.mobsoftstore.user;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

/**
 * Authentication service class
 */
@Service
public class AuthService {

    private final AuthenticationManager authManager;

    public AuthService(AuthenticationManager authManager) {
        this.authManager = authManager;
    }

    /**
     * Performs authentication using Spring's {@link AuthenticationManager}
     *
     * @param username User's username
     * @param password User's password
     * @return {@link Authentication} implementation instance
     * @throws AuthenticationException if an authentication exception occurs
     */
    public Authentication doAuth(String username, String password) throws AuthenticationException {
        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(username, password);
        return authManager.authenticate(authInputToken);
    }
}
