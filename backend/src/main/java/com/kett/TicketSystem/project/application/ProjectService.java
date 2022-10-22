package com.kett.TicketSystem.project.application;

import com.kett.TicketSystem.project.domain.Project;
import com.kett.TicketSystem.project.domain.NoProjectFoundException;
import com.kett.TicketSystem.project.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project getProjectById(UUID id) {
        return projectRepository
                .findById(id)
                .orElseThrow(() -> new NoProjectFoundException("could not find project with id: " + id));
    }
}
