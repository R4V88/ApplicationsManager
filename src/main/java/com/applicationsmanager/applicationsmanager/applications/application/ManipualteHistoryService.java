package com.applicationsmanager.applicationsmanager.applications.application;

import com.applicationsmanager.applicationsmanager.applications.application.port.ManipulateHistoryUseCase;
import com.applicationsmanager.applicationsmanager.applications.db.HistoryRepository;
import com.applicationsmanager.applicationsmanager.applications.domain.History;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class ManipualteHistoryService implements ManipulateHistoryUseCase {
    private final HistoryRepository repository;

    @Override
    public void insertNewHistory(History history) {
        final History save = repository.save(history);
        log.info("Saved new history record with id: " + save.getId());
    }
}
