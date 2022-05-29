package com.applicationsmanager.applicationsmanager.applications.application;

import com.applicationsmanager.applicationsmanager.applications.application.port.ManipulateApplicationUseCase;
import com.applicationsmanager.applicationsmanager.applications.db.ApplicationRepository;
import com.applicationsmanager.applicationsmanager.applications.domain.Application;
import com.applicationsmanager.applicationsmanager.applications.domain.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class ManipulateApplicationServiceTest {

    @Autowired
    ManipulateApplicationUseCase service;
    @Autowired
    ApplicationRepository repository;

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

        //WHEN
//        service.createApplication(title, content);

        //THEN
        final Optional<Application> app = repository.findAll()
                .stream()
                .filter(application -> application.getTitle().startsWith(title))
                .findFirst();

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
        String title = "Test";
        String content = """
                Test
                Content
                Line 3
                Line 4
                Line 5
                """;

//        service.createApplication(title, content);

        //WHEN
        final Optional<Application> app = service.findOneByTitle(title);

        //THEN
        assertEquals(title, app.get().getTitle());
        assertEquals(content, app.get().getContent());
        assertEquals(Status.CREATED, app.get().getStatus());
        assertEquals(0L, app.get().getVersion());
    }
}