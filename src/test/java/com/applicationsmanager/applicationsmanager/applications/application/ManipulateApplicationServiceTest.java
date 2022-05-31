package com.applicationsmanager.applicationsmanager.applications.application;

import com.applicationsmanager.applicationsmanager.applications.application.port.ManipulateApplicationUseCase;
import com.applicationsmanager.applicationsmanager.applications.application.port.ManipulateApplicationUseCase.CreateApplicationCommand;
import com.applicationsmanager.applicationsmanager.applications.application.port.ManipulateApplicationUseCase.CreateApplicationResponse;
import com.applicationsmanager.applicationsmanager.applications.application.port.ManipulateApplicationUseCase.UpdateContentCommand;
import com.applicationsmanager.applicationsmanager.applications.application.port.ManipulateApplicationUseCase.UpdateStatusCommand;
import com.applicationsmanager.applicationsmanager.applications.db.ApplicationRepository;
import com.applicationsmanager.applicationsmanager.applications.domain.Application;
import com.applicationsmanager.applicationsmanager.applications.domain.Status;
import com.applicationsmanager.applicationsmanager.applications.web.RestPaginatedApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ManipulateApplicationServiceTest {

    @Autowired
    private ManipulateApplicationUseCase service;
    @Autowired
    private ApplicationRepository repository;

    private final String title = "Title";
    private final String content = """
            Test
            Content
            Line 3
            Line 4
            Line 5
            """;

    @Test
    void successfullyCreateNewApplication() {
        //GIVEN
        String title = "Test";
        String content = """
                Test
                Content
                Line 3
                Line 4
                Line 5
                """;

        CreateApplicationCommand command = CreateApplicationCommand.builder()
                .title(title)
                .content(content)
                .build();

        //WHEN
        final CreateApplicationResponse response = service.createApplication(command);

        //THEN
        final Long applicationId = response.getRight();
        final Optional<Application> app = repository.findById(applicationId);

        assertEquals(title, app.get().getTitle());
        assertEquals(content, app.get().getContent());
        assertEquals(Status.CREATED, app.get().getStatus());
        assertEquals(0L, app.get().getVersion());
        assertNotNull(app.get().getCreatedAt());
        assertNotNull(app.get().getUpdatedAt());
        assertNull(app.get().getUuid());
        assertNull(app.get().getReason());
    }

    @Test
    void retrieveNewCreatedApplication() {
        //GIVEN
        final Long applicationId = givenApplication(title, content);

        //WHEN
        final Optional<Application> app = service.findById(applicationId);

        //THEN
        assertEquals(title, app.get().getTitle());
        assertEquals(content, app.get().getContent());
        assertEquals(Status.CREATED, app.get().getStatus());
        assertEquals(0L, app.get().getVersion());
    }

    @Test
    void shouldDeleteApplicationByGivenId() {
        //GIVEN
        final Long applicationId = givenApplication(title, content);

        UpdateStatusCommand command = UpdateStatusCommand.builder()
                .status(Status.DELETED)
                .reason("Test")
                .build();

        service.updateApplicationStatus(applicationId, command);

        //WHEN
        service.deleteApplicationById(applicationId);

        //THEN
        Optional<Application> optionalApplication = service.findById(applicationId);

        assertFalse(optionalApplication.isPresent());
    }

    @Test
    void shouldChangeApplicationContentWhenStatusCreated() {
        //GIVEN
        final Long applicationId = givenApplication(title, content);

        //WHEN
        UpdateContentCommand command = UpdateContentCommand.builder()
                .content("new test content")
                .build();

        service.changeApplicationContent(applicationId, command);

        //THEN
        final Optional<Application> app = service.findById(applicationId);
        assertEquals(command.getContent(), app.get().getContent());
    }

    @Test
    void shouldUpdateStatusWhenStatusCreated() {
        //GIVEN
        final Long applicationId = givenApplication(title, content);

        //WHEN
        UpdateStatusCommand command = UpdateStatusCommand.builder()
                .reason("")
                .status(Status.VERIFIED)
                .build();

        service.updateApplicationStatus(applicationId, command);
        //THEN

        final Optional<Application> app = service.findById(applicationId);
        assertEquals(command.getStatus(), app.get().getStatus());
    }

    @Test
    void shouldApplyUuidWhenStatusPublished() {
        //GIVEN
        final Long applicationId = givenApplication(title, content);

        //WHEN
        UpdateStatusCommand commandVerified = UpdateStatusCommand.builder()
                .reason("")
                .status(Status.VERIFIED)
                .build();

        UpdateStatusCommand commandAccepted = UpdateStatusCommand.builder()
                .reason("")
                .status(Status.ACCEPTED)
                .build();

        UpdateStatusCommand commandPublished = UpdateStatusCommand.builder()
                .reason("")
                .status(Status.PUBLISHED)
                .build();

        service.updateApplicationStatus(applicationId, commandVerified);
        service.updateApplicationStatus(applicationId, commandAccepted);
        service.updateApplicationStatus(applicationId, commandPublished);

        //THEN

        final Optional<Application> app = service.findById(applicationId);
        assertEquals(commandPublished.getStatus(), app.get().getStatus());
        assertNotNull(app.get().getUuid());
    }

    @Test
    void shouldNotChangeStatusFromVerifiedToDeleted() {
        //GIVEN
        final Long applicationId = givenApplication(title, content);

        //WHEN
        UpdateStatusCommand commandVerified = UpdateStatusCommand.builder()
                .reason("")
                .status(Status.VERIFIED)
                .build();

        service.updateApplicationStatus(applicationId, commandVerified);

        //THEN
        UpdateStatusCommand commandDeleted = UpdateStatusCommand.builder()
                .reason("")
                .status(Status.DELETED)
                .build();

        service.updateApplicationStatus(applicationId, commandDeleted);

        final Optional<Application> app = service.findById(applicationId);
        assertNotEquals(app.get().getStatus(), commandDeleted.getStatus());
    }

    @Test
    void shouldRetrieveTenElements() {
        //GIVEN
        for (int i = 0; i <= 20; i++) {
            givenApplication(title, content);
        }

        Pageable pageable = Pageable.ofSize(10);

        //WHEN
        final RestPaginatedApplication restPaginatedApplication = service
                .filterApplicationsByTitleAndStatus(null, null, pageable);

        //THEN
        assertEquals(10, restPaginatedApplication.getApplications().size());

    }

    private Long givenApplication(String title, String content) {
        CreateApplicationCommand command = CreateApplicationCommand
                .builder()
                .title(title)
                .content(content)
                .build();
        return service.createApplication(command).getRight();
    }
}