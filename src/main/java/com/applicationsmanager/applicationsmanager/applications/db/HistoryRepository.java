package com.applicationsmanager.applicationsmanager.applications.db;

import com.applicationsmanager.applicationsmanager.applications.domain.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long> {
}
