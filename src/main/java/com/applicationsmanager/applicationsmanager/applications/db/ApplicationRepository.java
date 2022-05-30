package com.applicationsmanager.applicationsmanager.applications.db;

import com.applicationsmanager.applicationsmanager.applications.domain.Application;
import com.applicationsmanager.applicationsmanager.applications.domain.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Page<Application> findAllByTitleContains(String tile, Pageable pageable);
    Page<Application> findAllByStatus(Status status, Pageable pageable);
    Page<Application> findAllByTitleContainsAndStatus(String title, Status status, Pageable pageable);
}
