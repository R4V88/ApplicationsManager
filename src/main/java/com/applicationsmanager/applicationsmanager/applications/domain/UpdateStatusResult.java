package com.applicationsmanager.applicationsmanager.applications.domain;

import lombok.Value;

@Value
public class UpdateStatusResult {
    Status status;

    static UpdateStatusResult ok(Status newStatus) {
        return new UpdateStatusResult(newStatus);
    }
}
