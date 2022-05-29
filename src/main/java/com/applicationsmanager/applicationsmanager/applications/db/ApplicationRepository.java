package com.applicationsmanager.applicationsmanager.applications.db;

import com.applicationsmanager.applicationsmanager.applications.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Optional<Application> findByName(String name);
}
