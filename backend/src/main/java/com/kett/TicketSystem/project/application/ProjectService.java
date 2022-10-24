package com.kett.TicketSystem.project.application;

import com.kett.TicketSystem.project.domain.Project;
import com.kett.TicketSystem.project.domain.Ticket;
import com.kett.TicketSystem.project.domain.exceptions.ImpossibleException;
import com.kett.TicketSystem.project.domain.exceptions.NoProjectFoundException;
import com.kett.TicketSystem.project.domain.exceptions.NoTicketFoundException;
import com.kett.TicketSystem.project.repository.ProjectRepository;
import com.kett.TicketSystem.project.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final TicketRepository ticketRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, TicketRepository ticketRepository) {
        this.projectRepository = projectRepository;
        this.ticketRepository = ticketRepository;
    }

    public Project getProjectById(UUID id) {
        return projectRepository
                .findById(id)
                .orElseThrow(() -> new NoProjectFoundException("could not find project with id: " + id));
    }

    public Ticket getTicketById(UUID id) {
        return ticketRepository
                .findById(id)
                .orElseThrow(() -> new NoTicketFoundException("could not find ticket with id: " + id));
    }

    public List<Ticket> getTicketsByProjectId(UUID id) {
        return projectRepository
                .findById(id)
                .orElseThrow(() -> new NoProjectFoundException("could not find project with id: " + id))
                .getTickets();
    }

    public Ticket getTicketByProjectIdAndTicketNumber(UUID id, UUID ticketNumber) {
        // TODO: id is not needed, because ticketNumber is unique. Maybe ticket should be its own aggregate.
        return ticketRepository
                .findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new NoTicketFoundException("could not find ticket with ticketNumber: " + ticketNumber));
    }

    public Project addProject(Project project) {
        return projectRepository.save(project);
    }

    public void deleteProjectById(UUID id) {
        Long numOfDeletedProjects = projectRepository.removeById(id);

        if (numOfDeletedProjects == 0) {
            throw new NoProjectFoundException("could not delete because there was no project with id: " + id);
        } else if (numOfDeletedProjects > 1) {
            throw new ImpossibleException("!!! This should not happen. " +
                    "Multiple projects were deleted when deleting project with id: " + id);
        }
    }

    public void patchProjectById(UUID id, Project patchData) {
        Project existingProject =
                projectRepository
                        .findById(id)
                        .orElseThrow(() -> new NoProjectFoundException("could not find project with id: " + id));


    }
}
