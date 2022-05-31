package com.applicationsmanager.applicationsmanager.applications.web;

import com.applicationsmanager.applicationsmanager.applications.domain.Application;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RestPaginatedApplication {
    private List<Application> applications;
    private Long numberOfItems;
    private int numberOfPages;
}
