package com.applicationsmanager.applicationsmanager.applications.application;

import com.applicationsmanager.applicationsmanager.applications.application.port.ManipulateApplicationUseCase;
import com.applicationsmanager.applicationsmanager.applications.application.port.ManipulateHistoryUseCase;
import com.applicationsmanager.applicationsmanager.applications.db.ApplicationRepository;
import com.applicationsmanager.applicationsmanager.applications.domain.Application;
import com.applicationsmanager.applicationsmanager.applications.domain.History;
import com.applicationsmanager.applicationsmanager.applications.domain.Status;
import com.applicationsmanager.applicationsmanager.applications.web.RestPaginatedApplication;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class ManipulateApplicationService implements ManipulateApplicationUseCase {
    private static final int MIN_REASON_LENGHT = 1;
    final ApplicationRepository applicationRepository;
    final ManipulateHistoryUseCase historyService;

    @Override
    public CreateApplicationResponse createApplication(CreateApplicationCommand command) {
        Application application = new Application(command.getTitle(), command.getContent());
        Application saveApplication = applicationRepository.save(application);
        History record = new History(application);
        historyService.insertNewHistory(record);
        log.info("Created new application with id: " + saveApplication.getId());
        return CreateApplicationResponse.success(saveApplication.getId());
    }

    @Override
    public Optional<Application> findById(Long id) {
        return applicationRepository.findById(id);
    }

    @Override
    public DeleteApplicationResponse deleteApplicationById(Long id) {
        final Optional<Application> application = findById(id);
        if (application.isPresent()) {
            if (application.get().getStatus().equals(Status.DELETED)) {
                applicationRepository.deleteById(id);
                log.info("Deleted application with id: " + id);
                return DeleteApplicationResponse.success();
            }
        }
        return DeleteApplicationResponse.failure(Error.BAD_REQUEST);
    }

    @Override
    public RestPaginatedApplication filterApplicationsByTitleAndStatus(String title, Status status, Pageable pageable) {
        if (title != null && status != null) {
            Page<Application> applications = applicationRepository.findAllByTitleContainsAndStatus(title, status, pageable);
            return toPaginatedApplication(applications);
        }

        if (title != null) {
            Page<Application> applications = applicationRepository.findAllByTitleContains(title, pageable);
            return toPaginatedApplication(applications);
        }

        if (status != null) {
            Page<Application> applications = applicationRepository.findAllByStatus(status, pageable);
            return toPaginatedApplication(applications);
        }
        return readBooks(pageable);
    }

    @Transactional
    @Override
    public UpadateContentResponse changeApplicationContent(Long id, UpdateContentCommand command) {
        final Optional<Application> application = applicationRepository.findById(id);
        final String content = command.getContent();

        if (application.isPresent()) {
            Application app = application.get();
            final Status status = app.getStatus();
            if (status.equals(Status.CREATED) || status.equals(Status.VERIFIED)) {
                app.setContent(content);
                saveApplicationAndHistory(app);
                log.info("Changed application with id: " + id + ", content: " + app.getContent() + " to: " + content);
                return UpadateContentResponse.success(content);
            }
        }
        return UpadateContentResponse.failure(Error.BAD_REQUEST);
    }

    @Transactional
    @Override
    public UpdateStatusResponse updateApplicationStatus(Long id, UpdateStatusCommand command) {
        return applicationRepository.findById(id)
                .map(application -> {
                    final Status status = command.getStatus();
                    final String reason = command.getReason();
                    final String currentStatus = application.getStatus().toString();

                    if (status.equals(Status.REJECTED) || status.equals(Status.DELETED)) {
                        if (reason.length() >= MIN_REASON_LENGHT) {
                            application.setReason(reason);
                            application.updateStatus(status);
                            saveApplicationAndHistory(application);
                            log.info("Changed application status: " + currentStatus + " with id: " + id + " to status: " + status);
                            return UpdateStatusResponse.success(application.getStatus());
                        } else {
                            log.info("Application id: " + id + ", unable to change status from " + currentStatus + " to status" + status);
                            return UpdateStatusResponse.failure(Error.BAD_REQUEST);
                        }
                    }
                    if (status.equals(Status.PUBLISHED)) {
                        long mostSignificantBits = getUuid();
                        log.info("Generated new UUID " + mostSignificantBits + "for application with id: " + id);
                        application.setUuid(mostSignificantBits);
                        application.updateStatus(status);
                        saveApplicationAndHistory(application);
                        log.info("Changed application status: " + currentStatus + " with id: " + id + " to status: " + status);
                        return UpdateStatusResponse.success(application.getStatus());
                    }

                    application.updateStatus(status);
                    saveApplicationAndHistory(application);
                    log.info("Changed application status: " + currentStatus + " with id: " + id + " to status: " + status);
                    return UpdateStatusResponse.success(application.getStatus());

                }).orElse(UpdateStatusResponse.failure(Error.NOT_FOUND));
    }

    private void saveApplicationAndHistory(Application application) {
        applicationRepository.save(application);
        History record = new History(application);
        historyService.insertNewHistory(record);
    }

    private long getUuid() {
        long mostSignificantBits = UUID.randomUUID().getMostSignificantBits();
        if (mostSignificantBits < 0) {
            mostSignificantBits *= -1;
        }
        return mostSignificantBits;
    }

    @Override
    public RestPaginatedApplication readBooks(Pageable pageable) {
        Page<Application> applications = applicationRepository.findAll(pageable);
        return toPaginatedApplication(applications);
    }

    private RestPaginatedApplication toPaginatedApplication(Page<Application> applications) {
        return RestPaginatedApplication.builder()
                .numberOfItems(applications.getTotalElements()).numberOfPages(applications.getTotalPages())
                .applications(applications.getContent())
                .build();
    }
}
