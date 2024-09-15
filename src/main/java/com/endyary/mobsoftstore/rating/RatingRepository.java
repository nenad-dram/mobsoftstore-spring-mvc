package com.endyary.mobsoftstore.rating;

import com.endyary.mobsoftstore.application.Application;
import com.endyary.mobsoftstore.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Application rating repository definition
 */
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> getByApplicationAndUser(Application application, User user);
}
