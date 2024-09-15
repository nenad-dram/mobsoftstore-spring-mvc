package com.endyary.mobsoftstore;


import com.endyary.mobsoftstore.config.MvcConfig;
import com.endyary.mobsoftstore.user.AuthService;
import com.endyary.mobsoftstore.user.Role;
import com.endyary.mobsoftstore.user.User;
import com.endyary.mobsoftstore.user.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MvcConfig.class})
@WebAppConfiguration
class UserTest {

    @Autowired
    UserService userService;

    @Autowired
    AuthService authService;

    @Test
    void findByUsername_validUsername_foundWithValidRole() {
        Optional<User> dbUser = userService.findByUsername("developer1");
        User user = null;
        if (dbUser.isPresent()) {
            user = dbUser.get();
        }
        assert user != null;
        Assertions.assertEquals(user.getRole(), Role.DEVELOPER);
    }

    @Test
    void findByUsername_invalidUsername_notFound() {
        Optional<User> dbUser = userService.findByUsername("developer123");
        Assertions.assertFalse(dbUser.isPresent());
    }

    @Test
    void authUser_validCredentials_authenticated() {
        String username = "developer1";
        String password = "password";
        GrantedAuthority roleDeveloper = new SimpleGrantedAuthority(Role.DEVELOPER.name());

        Authentication auth = authService.doAuth(username, password);
        Assertions.assertTrue(auth.getAuthorities().contains(roleDeveloper));
    }

    @Test
    void authUser_invalidCredentials_throwBadCredentials() {
        String username = "developer123";
        String password = "password";

        Assertions.assertThrows(BadCredentialsException.class, () -> authService.doAuth(username, password));
    }

}
