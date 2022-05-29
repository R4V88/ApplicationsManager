package com.applicationsmanager.applicationsmanager.applications.application.port;

import com.applicationsmanager.applicationsmanager.applications.entity.Application;

public interface ManipulateApplicationUseCase {
    void createApplication(String name, String content);

    void save(Application application);
}
