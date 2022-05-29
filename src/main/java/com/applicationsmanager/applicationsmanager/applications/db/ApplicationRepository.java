package com.applicationsmanager.applicationsmanager.applications.db;

import com.applicationsmanager.applicationsmanager.applications.domain.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
}
