package com.endyary.mobsoftstore.user;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * User service class
 */
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Returns {@link User} for the given username, if exists.
     *
     * @param username User's username
     * @return Optional User instance
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Performs authentication and returns User information.
     * Used by Spring Security during the authentication process.
     *
     * @param username User's username
     * @return Spring's UserDetails object
     * @throws UsernameNotFoundException if user can't be found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User principal = userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException(String.format("User with username - %s, not found", username)));
        return new org.springframework.security.core.userdetails.User(principal.getUsername(), principal.getPassword(),
                getGrantedAuthorities(principal));
    }

    /**
     * Returns roles ({@link SimpleGrantedAuthority}) for the given User.
     *
     * @param principal Authenticated user
     * @return Roles as a list of ({@link SimpleGrantedAuthority})
     */
    private List<SimpleGrantedAuthority> getGrantedAuthorities(User principal) {
        List<SimpleGrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(principal.getRole().name()));
        return roles;
    }

    /**
     * Saves user in the DB
     *
     * @param user {@link User}
     * @return Saved {@link User}
     */
    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * Returns the currently logged user with the help of Spring Security context
     *
     * @return Current {@link User}
     */
    public User getCurrentUser() {
        User user = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> userOptional = findByUsername(authentication.getName());
        if (userOptional.isPresent()) {
            user = userOptional.get();
        }
        return user;
    }

}
