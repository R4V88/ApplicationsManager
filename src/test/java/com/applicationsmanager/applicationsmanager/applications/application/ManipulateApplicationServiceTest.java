package com.applicationsmanager.applicationsmanager.applications.application;

import com.applicationsmanager.applicationsmanager.applications.application.port.ManipulateApplicationUseCase;
import com.applicationsmanager.applicationsmanager.applications.db.ApplicationRepository;
import com.applicationsmanager.applicationsmanager.applications.entity.Application;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ManipulateApplicationServiceTest {

    @Autowired
    ManipulateApplicationUseCase service;
    @Autowired
    ApplicationRepository repository;

    @Test
    void SuccessfullyCreateNewApplication() {
        //GIVEN
        String name = "Test";
        String content = """
                Test
                Content
                Line 3
                Line 4
                Line 5
                """;

        //WHEN
        service.createApplication(name, content);

        //THEN
        final Optional<Application> app = repository.findByName(name);

        if(app.isPresent()) {
            assertEquals(name, app.get().getName());
            assertEquals(content, app.get().getContent());
        }
    }

}