package com.kett.TicketSystem.membership.domain;

import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import com.kett.TicketSystem.membership.domain.consumedData.ProjectDataOfMembership;
import com.kett.TicketSystem.membership.domain.consumedData.UserDataOfMembership;
import com.kett.TicketSystem.membership.domain.events.LastProjectMemberDeletedEvent;
import com.kett.TicketSystem.membership.domain.events.MembershipAcceptedEvent;
import com.kett.TicketSystem.membership.domain.events.MembershipDeletedEvent;
import com.kett.TicketSystem.membership.domain.events.UnacceptedProjectMembershipCreatedEvent;
import com.kett.TicketSystem.membership.domain.exceptions.AlreadyLastAdminException;
import com.kett.TicketSystem.membership.domain.exceptions.MembershipAlreadyExistsException;
import com.kett.TicketSystem.membership.domain.exceptions.NoMembershipFoundException;
import com.kett.TicketSystem.membership.repository.MembershipRepository;
import com.kett.TicketSystem.common.exceptions.ImpossibleException;
import com.kett.TicketSystem.membership.repository.ProjectDataOfMembershipRepository;
import com.kett.TicketSystem.membership.repository.UserDataOfMembershipRepository;
import com.kett.TicketSystem.project.domain.events.DefaultProjectCreatedEvent;
import com.kett.TicketSystem.project.domain.events.ProjectCreatedEvent;
import com.kett.TicketSystem.project.domain.events.ProjectDeletedEvent;
import com.kett.TicketSystem.common.exceptions.NoProjectFoundException;
import com.kett.TicketSystem.user.domain.events.UserCreatedEvent;
import com.kett.TicketSystem.user.domain.events.UserDeletedEvent;
import com.kett.TicketSystem.common.exceptions.NoUserFoundException;
import com.kett.TicketSystem.user.domain.events.UserPatchedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class MembershipDomainService {
    private final MembershipRepository membershipRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final UserDataOfMembershipRepository userDataOfMembershipRepository;
    private final ProjectDataOfMembershipRepository projectDataOfMembershipRepository;

    @Autowired
    public MembershipDomainService(
            MembershipRepository membershipRepository,
            ApplicationEventPublisher eventPublisher,
            UserDataOfMembershipRepository userDataOfMembershipRepository,
            ProjectDataOfMembershipRepository projectDataOfMembershipRepository
    ) {
        this.membershipRepository = membershipRepository;
        this.eventPublisher = eventPublisher;
        this.userDataOfMembershipRepository = userDataOfMembershipRepository;
        this.projectDataOfMembershipRepository = projectDataOfMembershipRepository;
    }


    // create

    public Membership addNewMembership(Membership membership) throws MembershipAlreadyExistsException {
        if (!userDataOfMembershipRepository.existsByUserId(membership.getUserId())) {
            throw new NoUserFoundException("could not find user with id: " + membership.getUserId());
        }

        Membership initializedMembership = addMembership(membership);
        eventPublisher.publishEvent(
                new UnacceptedProjectMembershipCreatedEvent(
                        initializedMembership.getId(),
                        initializedMembership.getUserId(),
                        initializedMembership.getProjectId()
                )
        );

        return initializedMembership;
    }

    private Membership addDefaultMembership(Membership membership) throws MembershipAlreadyExistsException {
        membership.setState(State.ACCEPTED);
        Membership initializedMembership = addMembership(membership);
        eventPublisher.publishEvent(
                new MembershipAcceptedEvent(
                        initializedMembership.getId(),
                        initializedMembership.getProjectId(),
                        initializedMembership.getUserId()
                )
        );
        return initializedMembership;
    }

    private Membership addMembership(Membership membership) throws MembershipAlreadyExistsException {
        if (!projectDataOfMembershipRepository.existsByProjectId(membership.getProjectId())) {
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

    public List<Membership> getMembershipsByUserEmail(EmailAddress emailAddress) {
        return getMembershipsByUserId(
                getUserIdByUserEmailAddress(emailAddress)
        );
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

    public UUID getUserIdByUserEmailAddress(EmailAddress emailAddress) {
        List<UserDataOfMembership> userData = userDataOfMembershipRepository.findByUserEmailEquals(emailAddress);
        if (userData.isEmpty()) {
            throw new ImpossibleException("no user data found for user: " + emailAddress.toString());
        }
        return userData.get(0).getUserId();
    }


    // update

    public void updateMemberShipState(UUID id, State state) throws NoMembershipFoundException {
        Membership membership = this.getMembershipById(id);
        membership.setState(state);
        membershipRepository.save(membership);
        eventPublisher.publishEvent(
                new MembershipAcceptedEvent(
                        membership.getId(),
                        membership.getProjectId(),
                        membership.getUserId()
                )
        );
    }

    public void updateMembershipRole(UUID id, Role role) throws NoMembershipFoundException {
        Membership existingMembership = this.getMembershipById(id);
        Integer numOfActiveAdmins =
                membershipRepository
                        .countMembershipByProjectIdAndStateEqualsAndRoleEquals(
                                existingMembership.getProjectId(),
                                State.ACCEPTED,
                                Role.ADMIN
                        );
        if (existingMembership.isAccepted() && numOfActiveAdmins < 2 && role.equals(Role.MEMBER)) {
            throw new AlreadyLastAdminException(
                    "The membership with id: " + existingMembership.getId() + " " +
                    "cannot be degraded to " + role.toString() +
                    " because it is the last ADMIN of project with id: " + existingMembership.getProjectId()
            );
        }
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
            eventPublisher.publishEvent(
                    new LastProjectMemberDeletedEvent(
                            membership.getId(),
                            membership.getUserId(),
                            membership.getProjectId()
                    )
            );
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
    @Async
    public void handleProjectCreatedEvent(ProjectCreatedEvent projectCreatedEvent) {
        projectDataOfMembershipRepository.save(new ProjectDataOfMembership(projectCreatedEvent.getProjectId()));
        Membership defaultMembership = new Membership(
                projectCreatedEvent.getProjectId(),
                projectCreatedEvent.getUserId(),
                Role.ADMIN
        );
        this.addDefaultMembership(defaultMembership);
    }

    @EventListener
    @Async
    public void handleDefaultProjectCreatedEvent(DefaultProjectCreatedEvent defaultProjectCreatedEvent) {
        projectDataOfMembershipRepository.save(new ProjectDataOfMembership(defaultProjectCreatedEvent.getProjectId()));
        Membership defaultMembership = new Membership(
                defaultProjectCreatedEvent.getProjectId(),
                defaultProjectCreatedEvent.getUserId(),
                Role.ADMIN
        );
        this.addDefaultMembership(defaultMembership);
    }

    @EventListener
    public void handleProjectDeletedEvent(ProjectDeletedEvent projectDeletedEvent) {
        List<Membership> deletedMemberships = membershipRepository.deleteByProjectId(projectDeletedEvent.getProjectId());
        deletedMemberships.forEach(membership ->
                eventPublisher.publishEvent(
                        new MembershipDeletedEvent(membership.getId(), membership.getProjectId(), membership.getUserId())
                )
        );
        projectDataOfMembershipRepository.deleteByProjectId(projectDeletedEvent.getProjectId());
    }

    @EventListener
    @Async
    public void handleUserCreatedEvent(UserCreatedEvent userCreatedEvent) {
        if (!userDataOfMembershipRepository.existsByUserId(userCreatedEvent.getUserId())) {
            userDataOfMembershipRepository.save(new UserDataOfMembership(userCreatedEvent.getUserId(), userCreatedEvent.getEmailAddress()));
        }
    }

    @EventListener
    @Async
    public void handleUserPatchedEvent(UserPatchedEvent userPatchedEvent) {
        UserDataOfMembership userDataOfMembership =
                userDataOfMembershipRepository
                        .findByUserId(userPatchedEvent.getUserId())
                        .get(0);
        userDataOfMembership.setUserEmail(userPatchedEvent.getEmailAddress());
        userDataOfMembershipRepository.save(userDataOfMembership);
    }

    @EventListener
    public void handleUserDeletedEvent(UserDeletedEvent userDeletedEvent) {
        List<Membership> memberships = getMembershipsByUserId(userDeletedEvent.getUserId());
        memberships.forEach(membership -> {
            this.deleteMembershipById(membership.getId());
            eventPublisher.publishEvent(
                    new MembershipDeletedEvent(membership.getId(), membership.getProjectId(), membership.getUserId())
            );
        });
        userDataOfMembershipRepository.deleteByUserId(userDeletedEvent.getUserId());
    }
}
