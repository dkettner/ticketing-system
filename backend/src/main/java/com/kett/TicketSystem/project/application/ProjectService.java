package com.kett.TicketSystem.project.application;

import com.kett.TicketSystem.project.domain.Project;
import com.kett.TicketSystem.project.domain.Ticket;
import com.kett.TicketSystem.project.domain.TicketStatus;
import com.kett.TicketSystem.project.domain.exceptions.*;
import com.kett.TicketSystem.project.repository.ProjectRepository;
import com.kett.TicketSystem.project.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    private Ticket getTicketByTicketNumber(UUID ticketNumber) throws NoTicketFoundException {
        return ticketRepository
                .findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new NoTicketFoundException("could not find ticket with ticketNumber: " + ticketNumber));
    }

    public Project getProjectById(UUID id) throws NoProjectFoundException {
        return projectRepository
                .findById(id)
                .orElseThrow(() -> new NoProjectFoundException("could not find project with id: " + id));
    }

    public List<Ticket> getTicketsByProjectId(UUID id) {
        return getProjectById(id).getTickets();
    }
    // TODO: clean this up

    public Ticket getTicketByProjectIdAndTicketNumber(UUID id, UUID ticketNumber) {
        Boolean projectHasTicket = getProjectById(id).hasTicketWithTicketNumber(ticketNumber);
        if (!projectHasTicket) {
            throw new NoTicketFoundException(
                    "could not find ticket with ticketNumber: " + ticketNumber +
                    " in project with id: " + id
            );
        }
        return getTicketByTicketNumber(ticketNumber);
    }

    public Project addProject(Project project) {
        return projectRepository.save(project);
    }

    public void deleteProjectById(UUID id) {
        Long numOfDeletedProjects = projectRepository.removeById(id);

        if (numOfDeletedProjects == 0) {
            throw new NoProjectFoundException("could not delete because there was no project with id: " + id);
        } else if (numOfDeletedProjects > 1) {
            throw new ImpossibleException(
                    "!!! This should not happen. " +
                    "Multiple projects were deleted when deleting project with id: " + id
            );
        }
    }

    // TODO: clean this up
    public void patchProjectById(UUID id, String newName, String newDescription, List<UUID> newMemberIds) {
        if (newMemberIds != null && newMemberIds.isEmpty()) {
            throw new ProjectException(
                    "Cannot patch memberIds with empty list. " +
                    "If you do not want to patch memberIds, use null instead of empty list"
            );
        }

        Project existingProject = getProjectById(id);
        if (newName != null) {
            existingProject.setName(newName);
        }
        if (newDescription != null) {
            existingProject.setDescription(newDescription);
        }
        if (newMemberIds != null) {
            existingProject.setMemberIds(newMemberIds);
        }
        projectRepository.save(existingProject);
    }

    public Ticket addTicketToProject(UUID id, Ticket ticket) {
        Project project = getProjectById(id);

        // TODO: Check if this structure is needed or if addTicket could just use the "ticket"-parameter.
        project.addTicket(
                ticketRepository.save(ticket)
        );
        projectRepository.save(project);

        return ticket;
    }

    public void deleteTicketByProjectIdAndTicketNumber(UUID id, UUID ticketNumber) {
        Project project = getProjectById(id);
        project.removeTicketWithTicketNumber(ticketNumber);
        projectRepository.save(project);

        Long numOfDeletedTickets = ticketRepository.deleteByTicketNumber(ticketNumber);
        if (numOfDeletedTickets == 0 || numOfDeletedTickets > 1) {
            throw new ImpossibleException(
                    "!!! This should not happen. " +
                    "While trying to delete Ticket with ticketNumber: " + ticketNumber +
                    " the number of deleted tickets was: " + numOfDeletedTickets
            );
        }
    }

    // TODO: clean this up
    public void patchTicket(
            UUID id,
            UUID ticketNumber,
            String newTitle,
            String newDescription,
            LocalDateTime newDueTime,
            TicketStatus newTicketStatus,
            List<UUID> newAssigneeIds
    ) {
        if (newAssigneeIds != null && newAssigneeIds.isEmpty()) {
            throw new TicketException(
                    "Cannot patch assigneeIds with empty list. " +
                    "If you do not want to patch assigneeIds, use null instead of empty list."
            );
        }

        Boolean projectHasTicket = getProjectById(id).hasTicketWithTicketNumber(ticketNumber);
        if (!projectHasTicket) {
            throw new NoTicketFoundException(
                    "could not find ticket with ticketNumber: " + ticketNumber +
                    " in project with id: " + id
            );
        }

        Ticket existingTicket = getTicketByTicketNumber(ticketNumber);
        if (newTitle != null) {
            existingTicket.setTitle(newTitle);
        }
        if (newDescription != null) {
            existingTicket.setDescription(newDescription);
        }
        if (newDueTime != null) {
            existingTicket.setDueTime(newDueTime);
        }
        if (newTicketStatus != null) {
            existingTicket.setTicketStatus(newTicketStatus);
        }
        if (newAssigneeIds != null) {
            existingTicket.setAssigneeIds(newAssigneeIds);
        }
        ticketRepository.save(existingTicket);
    }
}
