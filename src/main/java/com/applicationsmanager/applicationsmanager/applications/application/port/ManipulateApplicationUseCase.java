package com.applicationsmanager.applicationsmanager.applications.application.port;

import com.applicationsmanager.applicationsmanager.applications.domain.Application;
import com.applicationsmanager.applicationsmanager.applications.domain.Status;
import com.applicationsmanager.applicationsmanager.applications.web.PaginatedApplicationResponse;
import com.applicationsmanager.applicationsmanager.commons.Either;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Optional;

public interface ManipulateApplicationUseCase {
    CreateApplicationResponse createApplication(CreateApplicationCommand command);

    Optional<Application> findOneByTitle(String title);

    Optional<Application> findById(Long id);

    PaginatedApplicationResponse readBooks(Pageable pageable);

    PaginatedApplicationResponse filterApplicationsByTitleAndStatus(String title, Status status, Pageable pageable);

    UpdateStatusResponse updateApplicationStatus(Long id, UpdateStatusCommand command);

    @Builder
    @Value
    class CreateApplicationCommand {
        @NotBlank
        String title;
        @NotBlank
        String content;
    }

    @Value
    class UpdateStatusCommand {
//        @NotNull
//        Long applicationId;
        Status status;
        String reason;
    }

    class CreateApplicationResponse extends Either<String, Long> {
        public CreateApplicationResponse(boolean success, String left, Long right) {
            super(success, left, right);
        }

        public static CreateApplicationResponse success(Long applicationId) {
            return new CreateApplicationResponse(true, null, applicationId);
        }

        public static CreateApplicationResponse failure(String error) {
            return new CreateApplicationResponse(false, error, null);
        }
    }

    class UpdateStatusResponse extends Either<Error, Status> {
        public UpdateStatusResponse(boolean success, Error left, Status right) {
            super(success, left, right);
        }

        public static UpdateStatusResponse success(Status status) {
            return new UpdateStatusResponse(true, null, status);
        }

        public static UpdateStatusResponse failure(Error error) {
            return new UpdateStatusResponse(false, error, null);
        }
    }

    @AllArgsConstructor
    @Getter
    enum Error {
        NOT_FOUND(HttpStatus.NOT_FOUND),
        FORBIDDEN(HttpStatus.FORBIDDEN),
        BAD_REQUEST(HttpStatus.BAD_REQUEST);

        private final HttpStatus status;
    }
}
