package com.kett.TicketSystem.ticket.application;

import com.kett.TicketSystem.common.exceptions.NoProjectFoundException;
import com.kett.TicketSystem.membership.domain.events.MembershipDeletedEvent;
import com.kett.TicketSystem.phase.domain.events.NewTicketAssignedToPhaseEvent;
import com.kett.TicketSystem.phase.domain.events.PhaseCreatedEvent;
import com.kett.TicketSystem.phase.domain.events.PhaseDeletedEvent;
import com.kett.TicketSystem.phase.domain.exceptions.UnrelatedPhaseException;
import com.kett.TicketSystem.project.domain.events.DefaultProjectCreatedEvent;
import com.kett.TicketSystem.project.domain.events.ProjectCreatedEvent;
import com.kett.TicketSystem.project.domain.events.ProjectDeletedEvent;
import com.kett.TicketSystem.ticket.domain.Ticket;
import com.kett.TicketSystem.ticket.domain.events.TicketCreatedEvent;
import com.kett.TicketSystem.ticket.domain.events.TicketDeletedEvent;
import com.kett.TicketSystem.ticket.domain.events.TicketPhaseUpdatedEvent;
import com.kett.TicketSystem.ticket.domain.exceptions.NoTicketFoundException;
import com.kett.TicketSystem.ticket.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final List<UUID> existingProjects;
    private final Hashtable<UUID, UUID> phaseProjectDict; // TODO: service is not stateless

    @Autowired
    public TicketService(TicketRepository ticketRepository, ApplicationEventPublisher eventPublisher) {
        this.ticketRepository = ticketRepository;
        this.eventPublisher = eventPublisher;
        this.existingProjects = new ArrayList<>();
        this.phaseProjectDict = new Hashtable<>();
    }


    // create

    public Ticket addTicket(Ticket ticket, UUID postingUserId) {
        if (!existingProjects.contains(ticket.getProjectId())) {
            throw new NoProjectFoundException("could not find project with id: " + ticket.getProjectId());
        }

        Ticket initializedTicket = ticketRepository.save(ticket);
        eventPublisher.publishEvent(new TicketCreatedEvent(initializedTicket.getId(), initializedTicket.getProjectId(), postingUserId));
        return initializedTicket;
    }


    // read

    public Ticket getTicketById(UUID id) throws NoTicketFoundException {
        return ticketRepository
                .findById(id)
                .orElseThrow(() -> new NoTicketFoundException("could not find ticket with id: " + id));
    }

    public List<Ticket> getTicketsByPhaseId(UUID phaseId) throws NoTicketFoundException {
        List<Ticket> tickets = ticketRepository.findByPhaseId(phaseId);
        if (tickets.isEmpty()) {
            throw new NoTicketFoundException("could not find tickets with phaseId: " + phaseId);
        }
        return tickets;
    }

    public List<Ticket> getTicketsByProjectId(UUID projectId) throws NoTicketFoundException {
        List<Ticket> tickets = ticketRepository.findByProjectId(projectId);
        if (tickets.isEmpty()) {
            throw new NoTicketFoundException("could not find tickets with projectId: " + projectId);
        }
        return tickets;
    }

    public List<Ticket> getTicketsByAssigneeId(UUID assigneeId) throws NoTicketFoundException {
        List<Ticket> tickets = ticketRepository.findByAssigneeIdsContaining(assigneeId);
        if (tickets.isEmpty()) {
            throw new NoTicketFoundException("could not find tickets with assigneeId: " + assigneeId);
        }
        return tickets;
    }

    public UUID getProjectIdByTicketId(UUID ticketId) throws NoTicketFoundException {
        return this.getTicketById(ticketId).getProjectId();
    }


    // update

    public void patchTicket(
            UUID id,
            String title,
            String description,
            LocalDateTime dueTime,
            UUID phaseId,
            List<UUID> assigneeIds
    ) throws NoTicketFoundException {
        Ticket ticket = this.getTicketById(id);

        if (title != null) {
            ticket.setTitle(title);
        }
        if (description != null) {
            ticket.setDescription(description);
        }
        if (dueTime != null) {
            ticket.setDueTime(dueTime);
        }
        if (phaseId != null) {
            if (!phaseBelongsToProject(phaseId, ticket.getProjectId())) {
                throw new UnrelatedPhaseException(
                        "The ticket with id: " + ticket.getId() +
                        " belongs to the project with id: " + ticket.getProjectId() + ". " +
                        "But the new phase with id: " + phaseId +
                        " does not."
                );
            }

            UUID oldPhaseId = ticket.getPhaseId();
            ticket.setPhaseId(phaseId);
            eventPublisher.publishEvent(new TicketPhaseUpdatedEvent(ticket.getId(), ticket.getProjectId(), oldPhaseId, phaseId));
        }
        if (assigneeIds != null) {
            ticket.setAssigneeIds(assigneeIds);
        }

        ticketRepository.save(ticket);
    }

    private Boolean phaseBelongsToProject(UUID phaseId, UUID projectIdCandidate) {
        UUID actualProjectId = phaseProjectDict.get(phaseId);
        return actualProjectId.equals(projectIdCandidate);
    }


    // delete

    public void deleteTicketById(UUID id) throws NoTicketFoundException {
        Ticket ticket = this.getTicketById(id);
        ticketRepository.removeById(id);

        eventPublisher.publishEvent(new TicketDeletedEvent(ticket.getId(), ticket.getProjectId(), ticket.getPhaseId()));
    }

    public void deleteTicketsByProjectId(UUID projectId) {
        ticketRepository.deleteByProjectId(projectId);
    }


    // event listeners

    @EventListener
    @Async
    public void handleMembershipDeletedEvent(MembershipDeletedEvent membershipDeletedEvent) {
        List<Ticket> tickets =
                ticketRepository
                        .findByProjectId(membershipDeletedEvent.getProjectId())
                        .stream()
                        .filter(ticket -> ticket.isAssignee(membershipDeletedEvent.getUserId()))
                        .toList();

        tickets.forEach(ticket -> ticket.removeAssignee(membershipDeletedEvent.getUserId()));
        ticketRepository.saveAll(tickets);
    }

    @EventListener
    @Async
    public void handleNewTicketAssignedToPhaseEvent(NewTicketAssignedToPhaseEvent newTicketAssignedToPhaseEvent) {
        Ticket ticket = this.getTicketById(newTicketAssignedToPhaseEvent.getTicketId());
        ticket.setPhaseId(newTicketAssignedToPhaseEvent.getPhaseId());
        ticketRepository.save(ticket);
    }

    @EventListener
    @Async
    public void handleProjectCreatedEvent(ProjectCreatedEvent projectCreatedEvent) {
        this.existingProjects.add(projectCreatedEvent.getProjectId());
    }

    @EventListener
    @Async
    public void handleDefaultProjectCreatedEvent(DefaultProjectCreatedEvent defaultProjectCreatedEvent) {
        this.existingProjects.add(defaultProjectCreatedEvent.getProjectId());
    }


    @EventListener
    @Async
    public void handleProjectDeletedEvent(ProjectDeletedEvent projectDeletedEvent) {
        this.deleteTicketsByProjectId(projectDeletedEvent.getProjectId());
        this.existingProjects.remove(projectDeletedEvent.getProjectId());
        this.phaseProjectDict.values().removeAll(Collections.singleton(projectDeletedEvent.getProjectId())); // TODO: needs thorough testing
    }

    @EventListener
    @Async
    public void handlePhaseCreatedEvent(PhaseCreatedEvent phaseCreatedEvent) {
        this.phaseProjectDict.put(phaseCreatedEvent.getPhaseId(), phaseCreatedEvent.getProjectId());
    }

    @EventListener
    @Async
    public void handlePhaseDeletedEvent(PhaseDeletedEvent phaseDeletedEvent) {
        this.phaseProjectDict.remove(phaseDeletedEvent.getPhaseId());
    }
}
