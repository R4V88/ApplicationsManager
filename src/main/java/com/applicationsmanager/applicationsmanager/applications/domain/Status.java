package com.applicationsmanager.applicationsmanager.applications.domain;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

public enum Status {
    CREATED {
        @Override
        public UpdateStatusResult updateStatus(Status status) {
            return switch (status) {
                case DELETED -> UpdateStatusResult.ok(DELETED);
                case VERIFIED -> UpdateStatusResult.ok(VERIFIED);
                default -> super.updateStatus(status);
            };
        }
    },
    VERIFIED {
        @Override
        public UpdateStatusResult updateStatus(Status status) {
            return switch (status) {
                case REJECTED -> UpdateStatusResult.ok(REJECTED);
                case ACCEPTED -> UpdateStatusResult.ok(ACCEPTED);
                default -> super.updateStatus(status);
            };
        }
    },
    REJECTED,
    ACCEPTED {
        @Override
        public UpdateStatusResult updateStatus(Status status) {
            return switch (status) {
                case REJECTED -> UpdateStatusResult.ok(REJECTED);
                case PUBLISHED -> UpdateStatusResult.ok(PUBLISHED);
                default -> super.updateStatus(status);
            };
        }
    },
    PUBLISHED,
    DELETED;

    public static Optional<Status> parseString(String value) {
        return Arrays.stream(values())
                .filter(it -> StringUtils.equalsIgnoreCase(it.name(), value))
                .findFirst();
    }

    public UpdateStatusResult updateStatus(Status status) {
        throw new IllegalArgumentException("Unable to mark " + this.name() + " application as " + status.name());
    }
}
