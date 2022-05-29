package com.applicationsmanager.applicationsmanager.applications.application;

import com.applicationsmanager.applicationsmanager.applications.application.port.ManipulateApplicationUseCase;
import com.applicationsmanager.applicationsmanager.applications.db.ApplicationRepository;
import com.applicationsmanager.applicationsmanager.applications.entity.Application;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ManipulateApplicationService implements ManipulateApplicationUseCase {
    final ApplicationRepository applicationRepository;

    @Override
    public void createApplication(String name, String content) {
        Application application = new Application(name, content);
        save(application);
    }

    @Override
    public void save(Application application) {
        applicationRepository.save(application);
    }
}
