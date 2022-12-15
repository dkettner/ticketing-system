package com.kett.TicketSystem.membership.application;

import com.kett.TicketSystem.membership.domain.Membership;
import com.kett.TicketSystem.membership.domain.Role;
import com.kett.TicketSystem.membership.domain.State;
import com.kett.TicketSystem.membership.domain.events.LastProjectMemberDeletedEvent;
import com.kett.TicketSystem.membership.domain.events.MembershipDeletedEvent;
import com.kett.TicketSystem.membership.domain.exceptions.MembershipAlreadyExistsException;
import com.kett.TicketSystem.membership.domain.exceptions.NoMembershipFoundException;
import com.kett.TicketSystem.membership.repository.MembershipRepository;
import com.kett.TicketSystem.common.exceptions.ImpossibleException;
import com.kett.TicketSystem.project.domain.events.DefaultProjectCreatedEvent;
import com.kett.TicketSystem.project.domain.events.ProjectCreatedEvent;
import com.kett.TicketSystem.project.domain.events.ProjectDeletedEvent;
import com.kett.TicketSystem.common.exceptions.NoProjectFoundException;
import com.kett.TicketSystem.user.domain.events.UserDeletedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MembershipService {
    private final MembershipRepository membershipRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final List<UUID> existingProjects; // TODO: service is not stateless, consumed events are not idempotent.

    @Autowired
    public MembershipService(MembershipRepository membershipRepository, ApplicationEventPublisher eventPublisher) {
        this.membershipRepository = membershipRepository;
        this.eventPublisher = eventPublisher;
        this.existingProjects = new ArrayList<>();
    }


    // create

    public Membership addMembership(Membership membership) throws MembershipAlreadyExistsException {
        if (!existingProjects.contains(membership.getProjectId())) {
            throw new NoProjectFoundException("could not find project with id: " + membership.getProjectId());
        }

        if (membershipRepository.existsByUserIdAndProjectId(membership.getUserId(), membership.getProjectId())) {
            throw new MembershipAlreadyExistsException(
                    "Membership for userId: " + membership.getUserId() +
                    " and projectId: " + membership.getProjectId() +
                    " already exists."
            );
        }
        return membershipRepository.save(membership);
    }


    // read

    public Membership getMembershipById(UUID id) throws NoMembershipFoundException {
        return membershipRepository
                .findById(id)
                .orElseThrow(() -> new NoMembershipFoundException("could not find membership with id: " + id));
    }

    public List<Membership> getMembershipsByUserId(UUID userId) throws NoMembershipFoundException {
        List<Membership> memberships =  membershipRepository.findByUserId(userId);
        if (memberships.isEmpty()) {
            throw new NoMembershipFoundException("could not find memberships with userId: " + userId);
        }
        return memberships;
    }

    public List<GrantedAuthority> getProjectAuthoritiesByUserId(UUID userId) {
        return membershipRepository
                .findByUserIdAndStateEquals(userId, State.ACCEPTED)
                .stream()
                .map(membership -> (GrantedAuthority) membership)
                .toList();
    }

    public List<Membership> getMembershipsByProjectId(UUID projectId) throws NoMembershipFoundException {
        List<Membership> memberships =  membershipRepository.findByProjectId(projectId);
        if (memberships.isEmpty()) {
            throw new NoMembershipFoundException("could not find memberships with projectId: " + projectId);
        }
        return memberships;
    }

    public UUID getUserIdByMembershipId(UUID id) throws NoMembershipFoundException {
        return this.getMembershipById(id).getUserId();
    }

    public UUID getProjectIdByMembershipId(UUID id) throws NoMembershipFoundException {
        return this.getMembershipById(id).getProjectId();
    }

    // TODO: Write query in repository for this?
    public Boolean allUsersAreProjectMembers(List<UUID> assigneeIds, UUID projectId) {
        HashSet<UUID> projectMemberIds =
                membershipRepository
                        .findByProjectId(projectId)
                        .stream()
                        .filter(membership -> membership.getState().equals(State.ACCEPTED))
                        .map(Membership::getUserId)
                        .collect(Collectors.toCollection(HashSet::new));

        return projectMemberIds.containsAll(assigneeIds);
    }


    // update

    public void patchMemberShipState(UUID id, State state) throws NoMembershipFoundException {
        Membership existingMembership = this.getMembershipById(id);
        existingMembership.setState(state);
        membershipRepository.save(existingMembership);
    }

    public void patchMembershipRole(UUID id, Role role) throws NoMembershipFoundException {
        Membership existingMembership = this.getMembershipById(id);
        existingMembership.setRole(role);
        membershipRepository.save(existingMembership);
    }


    // delete

    public void deleteMembershipById(UUID id) throws NoMembershipFoundException {
        Membership membership = this.getMembershipById(id);

        Long numOfDeletedMemberships = membershipRepository.removeById(id);
        if (numOfDeletedMemberships == 0) {
            throw new NoMembershipFoundException("could not delete because there was no membership with id: " + id);
        } else if (numOfDeletedMemberships > 1) {
            throw new ImpossibleException(
                    "!!! This should not happen. " +
                    "Multiple memberships were deleted when deleting membership with id: " + id
            );
        } else {
            eventPublisher.publishEvent(new MembershipDeletedEvent(id, membership.getProjectId(), membership.getUserId()));
        }

        // handle edge cases
        if (projectHasNoMembers(membership.getProjectId())) {
            eventPublisher.publishEvent(new LastProjectMemberDeletedEvent(membership.getUserId(), membership.getProjectId()));
        } else if (projectHasNoAdmins(membership.getProjectId())) {
            promoteRandomMemberToAdmin(membership.getProjectId());
        }
    }

    private Boolean projectHasNoMembers(UUID projectId) {
        return membershipRepository
                .findByProjectIdAndStateEquals(projectId, State.ACCEPTED)
                .isEmpty();
    }

    private Boolean projectHasNoAdmins(UUID projectId) {
        return membershipRepository
                .findByProjectIdAndStateEquals(projectId, State.ACCEPTED)
                .stream()
                .noneMatch(membership -> membership.getRole().equals(Role.ADMIN));
    }

    private void promoteRandomMemberToAdmin(UUID projectId) {
        List<Membership> memberships = this.getMembershipsByProjectId(projectId);
        Membership newAdmin = memberships.get(new Random().nextInt(memberships.size()));
        newAdmin.setRole(Role.ADMIN);
        membershipRepository.save(newAdmin);
    }


    // event listeners

    @EventListener
    public void handleProjectCreatedEvent(ProjectCreatedEvent projectCreatedEvent) {
        Membership defaultMembership = new Membership(
                projectCreatedEvent.getProjectId(),
                projectCreatedEvent.getUserId(),
                Role.ADMIN
        );
        defaultMembership.setState(State.ACCEPTED);
        this.addMembership(defaultMembership);
        this.existingProjects.add(projectCreatedEvent.getProjectId());
    }

    @EventListener
    public void handleDefaultProjectCreatedEvent(DefaultProjectCreatedEvent defaultProjectCreatedEvent) {
        Membership defaultMembership = new Membership(
                defaultProjectCreatedEvent.getProjectId(),
                defaultProjectCreatedEvent.getUserId(),
                Role.ADMIN
        );
        defaultMembership.setState(State.ACCEPTED);
        this.addMembership(defaultMembership);
    }

    @EventListener
    @Async
    public void handleProjectDeletedEvent(ProjectDeletedEvent projectDeletedEvent) {
        membershipRepository.deleteByProjectId(projectDeletedEvent.getProjectId());
        this.existingProjects.remove(projectDeletedEvent.getProjectId());
    }

    @EventListener
    @Async
    public void handleUserDeletedEvent(UserDeletedEvent userDeletedEvent) {
        List<Membership> memberships = getMembershipsByUserId(userDeletedEvent.getUserId());
        memberships.forEach(membership -> this.deleteMembershipById(membership.getId()));
    }
}
