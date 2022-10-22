package com.kett.TicketSystem;

import com.kett.TicketSystem.project.application.ProjectService;
import com.kett.TicketSystem.project.domain.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TicketSystemService {
    private final ProjectService projectService;

    @Autowired
    public TicketSystemService (ProjectService projectService) {
        this.projectService = projectService;
    }

    public Project getProjectById(UUID id) {
        return projectService.getProjectById(id); // TODO: Use DTO instead
    }
}
