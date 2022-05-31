package com.applicationsmanager.applicationsmanager.applications.web;

import com.applicationsmanager.applicationsmanager.applications.application.port.ManipulateApplicationUseCase;
import com.applicationsmanager.applicationsmanager.applications.application.port.ManipulateApplicationUseCase.CreateApplicationCommand;
import com.applicationsmanager.applicationsmanager.applications.application.port.ManipulateApplicationUseCase.UpdateStatusCommand;
import com.applicationsmanager.applicationsmanager.applications.domain.Status;
import com.applicationsmanager.applicationsmanager.web.CreatedURI;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

@AllArgsConstructor
@RestController
@RequestMapping("/api/applications")
public class ApplicationController {
    private final ManipulateApplicationUseCase manipulateApplication;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<Object> createApplication(@Valid @RequestBody CreateApplicationCommand command) {
        return manipulateApplication.createApplication(command)
                .handle(
                        applicationId -> ResponseEntity.created(appliationUri(applicationId)).build(),
                        error -> ResponseEntity.badRequest().body(error)
                );
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/search/filter")
    public ResponseEntity<Object> getApplicationsWithFilter(@RequestParam(required = false) String title, @RequestParam(required = false) String status, @PageableDefault Pageable pageable) {
        Status statusToEnum = null;
        if (status != null) {
            statusToEnum = Status.valueOf(status);
        }
        return ResponseEntity.ok(manipulateApplication.filterApplicationsByTitleAndStatus(title, statusToEnum, pageable));
    }

    @PostMapping("/{id}/status")
    public ResponseEntity<Object> updateApplicationStatus(@PathVariable Long id, @RequestBody UpdateStatusCommand command) {
//        Status statusToEnum = Status
//                .parseString(command.getStatus().toString())
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown status: " + command.getStatus()));

//        UpdateStatusCommand command = new UpdateStatusCommand(id, statusToEnum);

        return manipulateApplication.updateApplicationStatus(id, command)
                .handle(
                        newStatus -> ResponseEntity.accepted().build(),
                        error -> ResponseEntity.status(error.getStatus()).build()
                );
    }

    URI appliationUri(Long applicationId) {
        return new CreatedURI("/" + applicationId).uri();
    }
}
