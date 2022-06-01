package com.applicationsmanager.applicationsmanager.applications.web;

import com.applicationsmanager.applicationsmanager.applications.application.port.ManipulateApplicationUseCase;
import com.applicationsmanager.applicationsmanager.applications.application.port.ManipulateApplicationUseCase.CreateApplicationCommand;
import com.applicationsmanager.applicationsmanager.applications.application.port.ManipulateApplicationUseCase.CreateApplicationResponse;
import com.applicationsmanager.applicationsmanager.applications.application.port.ManipulateApplicationUseCase.UpdateApplicationResponse;
import com.applicationsmanager.applicationsmanager.applications.application.port.ManipulateApplicationUseCase.UpdateContentCommand;
import com.applicationsmanager.applicationsmanager.applications.application.port.ManipulateApplicationUseCase.UpdateStatusCommand;
import com.applicationsmanager.applicationsmanager.applications.domain.Status;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@AllArgsConstructor
@RestController
@RequestMapping("/api/applications")
public class ApplicationController {
    private final ManipulateApplicationUseCase manipulateApplication;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<Object> createApplication(@Valid @RequestBody RestCreateApplicationCommand command) {
        if (command.getContent().length() < 1 || command.getTitle().length() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Values are to short");
        }
        final CreateApplicationResponse application = manipulateApplication.createApplication(command.toCreateApplicationCommand());
        if (!application.isSuccess()) {
            String message = String.join(", ", application.getErrors());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        } else
            return ResponseEntity.ok(application.getId());
    }

    @GetMapping("/search/filter")
    public ResponseEntity<Object> getApplicationsWithFilter(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String status,
            @PageableDefault Pageable pageable) {

        Status statusToEnum = null;
        if (status != null) {
            statusToEnum = Status.valueOf(status);
        }

        return ResponseEntity.ok(manipulateApplication.filterApplicationsByTitleAndStatus(title, statusToEnum, pageable));
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PatchMapping("/{id}/status")
    public void updateApplicationStatus(@PathVariable Long id, @RequestBody RestUpdateStatusCommand command) {
        final UpdateApplicationResponse updateStatusResponse = manipulateApplication.updateApplicationStatus(id, command.toUpdateStatusCommand());
        if (!updateStatusResponse.isSuccess()) {
            String message = String.join(", ", updateStatusResponse.getErrors());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PatchMapping({"/{id}/content"})
    public void changeApplicationContent(@PathVariable Long id, @RequestBody RestUpdateContentCommand command) {
        if (command.getContent().length() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Content is too short");
        }
        final UpdateApplicationResponse updateContentResponse = manipulateApplication.changeApplicationContent(id, command.toUpdateContentCommand());
        if (!updateContentResponse.isSuccess()) {
            String message = String.join(", ", updateContentResponse.getErrors());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteApplication(@PathVariable Long id) {
        final boolean value = manipulateApplication.deleteApplicationById(id);
        if (!value) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong application id");
        }
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    static class RestCreateApplicationCommand {
        String title;
        String content;

        CreateApplicationCommand toCreateApplicationCommand() {
            return new CreateApplicationCommand(title, content);
        }
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    static class RestUpdateStatusCommand {
        Status status;
        String reason;

        UpdateStatusCommand toUpdateStatusCommand() {
            return new UpdateStatusCommand(status, reason);
        }
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    static class RestUpdateContentCommand {
        String content;

        UpdateContentCommand toUpdateContentCommand() {
            return new UpdateContentCommand(content);
        }
    }
}
