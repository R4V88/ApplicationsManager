package com.applicationsmanager.applicationsmanager.applications.web;

import com.applicationsmanager.applicationsmanager.applications.application.port.ManipulateApplicationUseCase;
import com.applicationsmanager.applicationsmanager.applications.application.port.ManipulateApplicationUseCase.CreateApplicationCommand;
import com.applicationsmanager.applicationsmanager.applications.application.port.ManipulateApplicationUseCase.UpdateContentCommand;
import com.applicationsmanager.applicationsmanager.applications.application.port.ManipulateApplicationUseCase.UpdateStatusCommand;
import com.applicationsmanager.applicationsmanager.applications.db.ApplicationRepository;
import com.applicationsmanager.applicationsmanager.applications.db.HistoryRepository;
import com.applicationsmanager.applicationsmanager.applications.domain.Application;
import com.applicationsmanager.applicationsmanager.applications.domain.History;
import com.applicationsmanager.applicationsmanager.applications.domain.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ApplicationControllerIT {
    @Autowired
    ApplicationController applicationController;
    @Autowired
    ManipulateApplicationUseCase applicationService;
    @Autowired
    ApplicationRepository applicationRepository;
    @Autowired
    HistoryRepository historyRepository;

    @Test
    void createApplication() {
        //GIVEN
        CreateApplicationCommand command = CreateApplicationCommand.builder()
                .content("Test Content")
                .title("Test Title")
                .build();

        //WHEN
        applicationController.createApplication(command);

        //THEN
        final List<Application> applicationList = applicationRepository.findAll();
        final List<History> historyList = historyRepository.findAll();
        assertEquals(1, applicationList.size());
        assertEquals(command.getTitle(), applicationList.get(0).getTitle());
        assertEquals(command.getContent(), applicationList.get(0).getContent());
        assertEquals(1, historyList.size());
        assertEquals(command.getTitle(), historyList.get(0).getTitle());
        assertEquals(command.getContent(), historyList.get(0).getContent());
    }

    @Test
    void updateStatusToVerified() {
        //GIVEN
        Application application = givenApplication();
        final Application save = applicationRepository.save(application);

        //WHEN
        UpdateStatusCommand command = getUpdateStatusCommand(Status.VERIFIED);
        applicationController.updateApplicationStatus(save.getId(), command);

        //THEN
        final Optional<Application> app = applicationRepository.findById(save.getId());
        assertEquals(Status.VERIFIED, app.get().getStatus());
    }

    @Test
    void updateStatusToRejected() {
        //GIVEN
        Application application = givenApplication();
        final Application save = applicationRepository.save(application);

        //WHEN
        UpdateStatusCommand commandVerified = getUpdateStatusCommand(Status.VERIFIED);
        UpdateStatusCommand commandRejeceted = getUpdateStatusCommand(Status.REJECTED);
        applicationController.updateApplicationStatus(save.getId(), commandVerified);
        applicationController.updateApplicationStatus(save.getId(), commandRejeceted);

        //THEN
        final Optional<Application> app = applicationRepository.findById(save.getId());
        assertEquals(Status.REJECTED, app.get().getStatus());
        assertEquals(commandRejeceted.getReason(), app.get().getReason());
    }

    @Test
    void updateStatusToPublished() {
        //GIVEN
        Application application = givenApplication();
        final Application givenApplication = applicationRepository.save(application);
        historyRepository.save(new History(givenApplication));

        //WHEN
        UpdateStatusCommand commandVerified = getUpdateStatusCommand(Status.VERIFIED);
        UpdateStatusCommand commandAccepted = getUpdateStatusCommand(Status.ACCEPTED);
        UpdateStatusCommand commandPublished = getUpdateStatusCommand(Status.PUBLISHED);
        applicationController.updateApplicationStatus(givenApplication.getId(), commandVerified);
        applicationController.updateApplicationStatus(givenApplication.getId(), commandAccepted);
        applicationController.updateApplicationStatus(givenApplication.getId(), commandPublished);

        //THEN
        final Optional<Application> app = applicationRepository.findById(givenApplication.getId());
        final List<History> historyList = historyRepository.findAll();
        assertEquals(Status.PUBLISHED, app.get().getStatus());
        assertNotNull(app.get().getUuid());
        final History hist = historyList
                .stream()
                .filter(history -> history.getStatus().equals(app.get().getStatus()))
                .filter(history -> history.getApplicationId().equals(app.get().getId()))
                .findFirst().get();
        assertEquals(app.get().getUuid(), hist.getUuid());
        assertEquals(4, historyList.size());
    }

    @Test
    void deleteApplication() {
        //GIVEN
        Application application = givenApplication();
        final Application save = applicationRepository.save(application);

        UpdateStatusCommand command = getUpdateStatusCommand(Status.DELETED);
        applicationService.updateApplicationStatus(save.getId(), command);

        //WHEN
        applicationController.deleteApplication(save.getId());

        //THEN
        final List<Application> applicationList = applicationRepository.findAll();
        assertEquals(0, applicationList.size());
    }

    @Test
    void updateApplicationContent() {
        //GIVEN
        Application application = givenApplication();
        final Application save = applicationRepository.save(application);

        //WHEN
        UpdateContentCommand command = UpdateContentCommand.builder()
                .content("Test Content DLC")
                .build();
        applicationController.changeApplicationContent(save.getId(), command);

        //THEN
        final Optional<Application> app = applicationRepository.findById(save.getId());
        assertNotEquals(save.getContent(), command.getContent());
        assertEquals(command.getContent(), app.get().getContent());
    }

    @Test
    void getApplicationWithFilter() {
        //GIVEN
        Application application = givenApplication();
        applicationRepository.save(application);

        //WHEN
        final ResponseEntity<Object> applicationsWithFilter = applicationController
                .getApplicationsWithFilter(null, Status.DELETED.toString(), Pageable.ofSize(10));

        //THEN
        assertEquals(0, ((RestPaginatedApplication) applicationsWithFilter.getBody()).getNumberOfItems());
    }

    @Test
    void getValidApplicationWithFilter() {
        //GIVEN
        Application application = givenApplication();
        applicationRepository.save(application);

        //WHEN
        final String title = "Test Title";
        final String status = Status.CREATED.toString();
        final ResponseEntity<Object> applicationsWithFilter = applicationController
                .getApplicationsWithFilter(title, status, Pageable.ofSize(10));

        //THEN
        assertEquals(1, ((RestPaginatedApplication) applicationsWithFilter.getBody()).getNumberOfItems());
        assertEquals(status, ((RestPaginatedApplication) applicationsWithFilter.getBody()).getApplications().get(0).getStatus().toString());
        assertEquals(title, ((RestPaginatedApplication) applicationsWithFilter.getBody()).getApplications().get(0).getTitle());
        assertEquals(1, ((RestPaginatedApplication) applicationsWithFilter.getBody()).getNumberOfPages());
    }

    private UpdateStatusCommand getUpdateStatusCommand(Status status) {
        return UpdateStatusCommand.builder()
                .status(status)
                .reason("Test reason")
                .build();
    }

    private Application givenApplication() {
        Application application = new Application();
        application.setTitle("Test Title");
        application.setContent("Test Content");
        application.setCreatedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());
        application.setStatus(Status.CREATED);
        application.setVersion(0L);
        return application;
    }
}