package com.kett.TicketSystem.ticket.domain;

import com.kett.TicketSystem.common.exceptions.NoProjectFoundException;
import com.kett.TicketSystem.membership.domain.events.MembershipAcceptedEvent;
import com.kett.TicketSystem.membership.domain.events.MembershipDeletedEvent;
import com.kett.TicketSystem.common.exceptions.InvalidProjectMembersException;
import com.kett.TicketSystem.phase.domain.events.PhaseCreatedEvent;
import com.kett.TicketSystem.phase.domain.events.PhaseDeletedEvent;
import com.kett.TicketSystem.common.exceptions.UnrelatedPhaseException;
import com.kett.TicketSystem.phase.domain.events.PhasePositionUpdatedEvent;
import com.kett.TicketSystem.project.domain.events.DefaultProjectCreatedEvent;
import com.kett.TicketSystem.project.domain.events.ProjectCreatedEvent;
import com.kett.TicketSystem.project.domain.events.ProjectDeletedEvent;
import com.kett.TicketSystem.ticket.domain.consumedData.*;
import com.kett.TicketSystem.ticket.domain.events.*;
import com.kett.TicketSystem.ticket.domain.exceptions.NoTicketFoundException;
import com.kett.TicketSystem.ticket.repository.MembershipDataOfTicketRepository;
import com.kett.TicketSystem.ticket.repository.ProjectDataOfTicketRepository;
import com.kett.TicketSystem.ticket.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class TicketDomainService {
    private final TicketRepository ticketRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ProjectDataOfTicketRepository projectDataOfTicketRepository;
    private final MembershipDataOfTicketRepository membershipDataOfTicketRepository;
    private final ConsumedPhaseDataManager consumedPhaseDataManager;

    @Autowired
    public TicketDomainService(
            TicketRepository ticketRepository,
            ApplicationEventPublisher eventPublisher,
            ProjectDataOfTicketRepository projectDataOfTicketRepository,
            MembershipDataOfTicketRepository membershipDataOfTicketRepository
    ) {
        this.ticketRepository = ticketRepository;
        this.eventPublisher = eventPublisher;
        this.projectDataOfTicketRepository = projectDataOfTicketRepository;
        this.membershipDataOfTicketRepository = membershipDataOfTicketRepository;
        this.consumedPhaseDataManager = new ConsumedPhaseDataManager();
    }


    // create

    public Ticket addTicket(Ticket ticket, UUID postingUserId) throws NoProjectFoundException, InvalidProjectMembersException {
        if (!projectDataOfTicketRepository.existsByProjectId(ticket.getProjectId())) {
            throw new NoProjectFoundException("could not find project with id: " + ticket.getProjectId());
        }
        if (!allAssigneesAreProjectMembers(ticket.getProjectId(), ticket.getAssigneeIds())) {
            throw new InvalidProjectMembersException(
                    "not all assignees are part of the project with id: " + ticket.getProjectId()
            );
        }

        // set phaseId of ticket
        UUID firstPhaseOfProjectId =
                consumedPhaseDataManager
                        .getFirstPhaseByProjectId(ticket.getProjectId())
                        .id();
        ticket.setPhaseId(firstPhaseOfProjectId);

        Ticket initializedTicket = ticketRepository.save(ticket);
        eventPublisher.publishEvent(new TicketCreatedEvent(initializedTicket.getId(), initializedTicket.getProjectId(), postingUserId));
        return initializedTicket;
    }

    private Boolean allAssigneesAreProjectMembers(UUID projectId, List<UUID> assigneeIds) {
        return assigneeIds
                .stream()
                .allMatch(assigneeId ->
                        membershipDataOfTicketRepository.existsByUserIdAndProjectId(assigneeId, projectId)
                );
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
    ) throws NoTicketFoundException, InvalidProjectMembersException, UnrelatedPhaseException {
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
            if (!allAssigneesAreProjectMembers(ticket.getProjectId(), assigneeIds)) {
                throw new InvalidProjectMembersException(
                        "not all assignees are part of the project with id: " + ticket.getProjectId()
                );
            }
            publishAssignmentEvents(ticket, assigneeIds, ticket.getAssigneeIds());
            ticket.setAssigneeIds(assigneeIds);
        }

        ticketRepository.save(ticket);
    }

    private Boolean phaseBelongsToProject(UUID phaseId, UUID projectIdCandidate) {
        Optional<PhaseVO> phaseVO = consumedPhaseDataManager.get(phaseId);
        return phaseVO
                .map(it -> it.projectId().equals(projectIdCandidate))
                .orElse(false);
    }

    private void publishAssignmentEvents(Ticket ticket, List<UUID> newAssignees, List<UUID> oldAssignees) {
        newAssignees
                .stream()
                .filter(assigneeId -> !oldAssignees.contains(assigneeId))
                .forEach(assigneeId ->
                        eventPublisher.publishEvent(
                                new TicketAssignedEvent(ticket.getId(), ticket.getProjectId(), assigneeId)
                        )
                );

        oldAssignees
                .stream()
                .filter(assigneeId -> !newAssignees.contains(assigneeId))
                .forEach(assigneeId ->
                        eventPublisher.publishEvent(
                                new TicketUnassignedEvent(ticket.getId(), ticket.getProjectId(), assigneeId)
                        )
                );
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

        membershipDataOfTicketRepository.deleteByMembershipId(membershipDeletedEvent.getMembershipId());
    }

    @EventListener
    @Async
    public void handleMembershipAcceptedEvent(MembershipAcceptedEvent membershipAcceptedEvent) {
        if (!projectDataOfTicketRepository.existsByProjectId(membershipAcceptedEvent.getProjectId())){
            projectDataOfTicketRepository.save(new ProjectDataOfTicket(membershipAcceptedEvent.getProjectId()));
        }
        membershipDataOfTicketRepository.save(
                new MembershipDataOfTicket(
                        membershipAcceptedEvent.getMembershipId(),
                        membershipAcceptedEvent.getUserId(),
                        membershipAcceptedEvent.getProjectId()
                )
        );
    }

    @EventListener
    @Async
    public void handleProjectCreatedEvent(ProjectCreatedEvent projectCreatedEvent) {
        if (!projectDataOfTicketRepository.existsByProjectId(projectCreatedEvent.getProjectId())){
            projectDataOfTicketRepository.save(new ProjectDataOfTicket(projectCreatedEvent.getProjectId()));
        }
    }

    @EventListener
    @Async
    public void handleDefaultProjectCreatedEvent(DefaultProjectCreatedEvent defaultProjectCreatedEvent) {
        if (!projectDataOfTicketRepository.existsByProjectId(defaultProjectCreatedEvent.getProjectId())) {
            projectDataOfTicketRepository.save(new ProjectDataOfTicket(defaultProjectCreatedEvent.getProjectId()));
        }
    }


    @EventListener
    @Async
    public void handleProjectDeletedEvent(ProjectDeletedEvent projectDeletedEvent) {
        this.deleteTicketsByProjectId(projectDeletedEvent.getProjectId());
        projectDataOfTicketRepository.deleteByProjectId(projectDeletedEvent.getProjectId());
        this.consumedPhaseDataManager.removeByPredicate(phaseVO ->
                phaseVO.projectId().equals(projectDeletedEvent.getProjectId())
        );
    }

    @EventListener
    @Async
    public void handlePhaseCreatedEvent(PhaseCreatedEvent phaseCreatedEvent) {
        this.consumedPhaseDataManager.add(
                new PhaseVO(
                        phaseCreatedEvent.getPhaseId(),
                        phaseCreatedEvent.getPreviousPhaseId(),
                        phaseCreatedEvent.getProjectId()
                )
        );
    }

    @EventListener
    public void handlePhasePositionUpdatedEvent(PhasePositionUpdatedEvent phasePositionUpdatedEvent) {
        consumedPhaseDataManager.overwrite(
                new PhaseVO(
                        phasePositionUpdatedEvent.getId(),
                        phasePositionUpdatedEvent.getPreviousPhaseId(),
                        phasePositionUpdatedEvent.getProjectId()
                )
        );
    }

    @EventListener
    @Async
    public void handlePhaseDeletedEvent(PhaseDeletedEvent phaseDeletedEvent) {
        this.consumedPhaseDataManager.remove(phaseDeletedEvent.getPhaseId());
    }
}
