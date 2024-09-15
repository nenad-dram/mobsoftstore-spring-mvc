package com.endyary.mobsoftstore.application;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Application repository definition
 */
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByCategory(Category category);

    boolean existsByName(String name);

    List<Application> findTop5ByOrderByDownloadCountDesc();
}
