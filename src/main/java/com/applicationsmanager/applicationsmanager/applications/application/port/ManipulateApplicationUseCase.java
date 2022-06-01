package com.applicationsmanager.applicationsmanager.applications.application.port;

import com.applicationsmanager.applicationsmanager.applications.domain.Application;
import com.applicationsmanager.applicationsmanager.applications.domain.Status;
import com.applicationsmanager.applicationsmanager.applications.web.RestPaginatedApplication;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

public interface ManipulateApplicationUseCase {
    CreateApplicationResponse createApplication(CreateApplicationCommand command);

    Optional<Application> findById(Long id);

    boolean deleteApplicationById(Long id);

    RestPaginatedApplication readBooks(Pageable pageable);

    RestPaginatedApplication filterApplicationsByTitleAndStatus(String title, Status status, Pageable pageable);

    UpdateApplicationResponse changeApplicationContent(Long id, UpdateContentCommand command);

    UpdateApplicationResponse updateApplicationStatus(Long id, UpdateStatusCommand command);

    @AllArgsConstructor
    @Getter
    enum Error {
        NOT_FOUND(HttpStatus.NOT_FOUND),
        FORBIDDEN(HttpStatus.FORBIDDEN),
        BAD_REQUEST(HttpStatus.BAD_REQUEST);

        private final HttpStatus status;
    }

    @Value
    class CreateApplicationCommand {
        String title;
        String content;
    }

    @Value
    class UpdateStatusCommand {
        Status status;
        String reason;
    }

    @Value
    class UpdateContentCommand {
        String content;
    }

    @Value
    class UpdateApplicationResponse {
        public static UpdateApplicationResponse SUCCESS = new UpdateApplicationResponse(true, emptyList());
        boolean success;
        List<String> errors;
    }

    @Value
    class CreateApplicationResponse {
        public static CreateApplicationResponse SUCCESS = new CreateApplicationResponse(true, emptyList(), null);
        boolean success;
        List<String> errors;
        Long id;
    }
}
