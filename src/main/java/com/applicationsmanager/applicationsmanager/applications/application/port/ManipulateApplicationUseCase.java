package com.applicationsmanager.applicationsmanager.applications.application.port;

import com.applicationsmanager.applicationsmanager.applications.domain.Application;

import java.util.Optional;

public interface ManipulateApplicationUseCase {
    void createApplication(String name, String content);

    void save(Application application);

    Optional<Application> findOneByTitle(String title);

    Optional<Application> findById(Long id);
}
