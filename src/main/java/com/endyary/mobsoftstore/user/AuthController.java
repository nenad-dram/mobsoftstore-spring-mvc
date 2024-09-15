package com.endyary.mobsoftstore.user;

import com.endyary.mobsoftstore.config.View;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Authentication controller class
 */
@Controller
public class AuthController {

    private final AuthenticationTrustResolver authenticationTrustResolver;

    public AuthController(AuthenticationTrustResolver authenticationTrustResolver) {
        this.authenticationTrustResolver = authenticationTrustResolver;
    }

    @GetMapping("/login")
    public String loginPage() {
        if (isCurrentAuthenticationAnonymous()) {
            return View.LOGIN.toString();
        } else {
            return "redirect:/";
        }
    }

    /**
     * Returns true if the user is already authenticated, else false
     */
    private boolean isCurrentAuthenticationAnonymous() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authenticationTrustResolver.isAnonymous(authentication);
    }
}
