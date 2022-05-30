package com.applicationsmanager.applicationsmanager.applications.application;

import com.applicationsmanager.applicationsmanager.applications.application.port.ManipulateApplicationUseCase;
import com.applicationsmanager.applicationsmanager.applications.db.ApplicationRepository;
import com.applicationsmanager.applicationsmanager.applications.domain.Application;
import com.applicationsmanager.applicationsmanager.applications.domain.Status;
import com.applicationsmanager.applicationsmanager.applications.web.PaginatedApplicationResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Override
    public PaginatedApplicationResponse filterApplicationsByTitleAndStatus(String title, Status status, Pageable pageable) {
        if (title != null && status != null) {
            Page<Application> applications = applicationRepository.findAllByTitleContainsAndStatus(title, status, pageable);
            return paginatedApplicationResponse(applications);
        }

        if (title != null) {
            Page<Application> applications = applicationRepository.findAllByTitleContains(title, pageable);
            return paginatedApplicationResponse(applications);
        }

        if (status != null) {
            Page<Application> applications = applicationRepository.findAllByStatus(status, pageable);
            return paginatedApplicationResponse(applications);
        }
        return readBooks(pageable);
    }

    @Override
    public PaginatedApplicationResponse readBooks(Pageable pageable) {
        Page<Application> applications = applicationRepository.findAll(pageable);
        return paginatedApplicationResponse(applications);
    }

    private PaginatedApplicationResponse paginatedApplicationResponse(Page<Application> applications) {
        return PaginatedApplicationResponse.builder()
                .numberOfItems(applications.getTotalElements()).numberOfPages(applications.getTotalPages())
                .applications(applications.getContent())
                .build();
    }
}
