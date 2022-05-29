package com.applicationsmanager.applicationsmanager.applications.application;

import com.applicationsmanager.applicationsmanager.applications.application.port.ManipulateApplicationUseCase;
import com.applicationsmanager.applicationsmanager.applications.db.ApplicationRepository;
import com.applicationsmanager.applicationsmanager.applications.domain.Application;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class ManipulateApplicationService implements ManipulateApplicationUseCase {
    final ApplicationRepository applicationRepository;

    @Override
    public CreateApplicationResponse createApplication(CreateApplicationCommand command) {
        Application application = new Application(command.getTitle(), command.getContent());
        Application saveApplication = applicationRepository.save(application);
        log.info("Created new application with id: " + saveApplication.getId());
        return CreateApplicationResponse.success(saveApplication.getId());
    }

    @Override
    public Optional<Application> findOneByTitle(String title) {
        return applicationRepository.findAll()
                .stream()
                .filter(application -> application.getTitle().startsWith(title))
                .findFirst();
    }

    @Override
    public Optional<Application> findById(Long id) {
        return applicationRepository.findById(id);
    }


}
