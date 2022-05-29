package com.applicationsmanager.applicationsmanager.applications.application.port;

import com.applicationsmanager.applicationsmanager.applications.domain.Application;
import com.applicationsmanager.applicationsmanager.commons.Either;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotEmpty;
import java.util.Optional;

public interface ManipulateApplicationUseCase {
    CreateApplicationResponse createApplication(CreateApplicationCommand command);

    Optional<Application> findOneByTitle(String title);

    Optional<Application> findById(Long id);

    @Builder
    @Value
    class CreateApplicationCommand {
        @NotEmpty
        String title;
        @NotEmpty
        String content;
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
}
