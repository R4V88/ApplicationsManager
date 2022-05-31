package com.applicationsmanager.applicationsmanager.applications.application.port;

import com.applicationsmanager.applicationsmanager.applications.domain.History;

public interface ManipulateHistoryUseCase {
    void insertNewHistory(History history);
}
