package com.kett.TicketSystem.membership.application;

import com.kett.TicketSystem.membership.domain.Membership;
import com.kett.TicketSystem.membership.domain.Role;
import com.kett.TicketSystem.membership.domain.State;
import com.kett.TicketSystem.membership.domain.exceptions.MembershipAlreadyExistsException;
import com.kett.TicketSystem.membership.domain.exceptions.NoMembershipFoundException;
import com.kett.TicketSystem.membership.repository.MembershipRepository;
import com.kett.TicketSystem.application.exceptions.ImpossibleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MembershipService {
    private final MembershipRepository membershipRepository;

    @Autowired
    public MembershipService(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    public Membership getMembershipById(UUID id) throws NoMembershipFoundException {
        return membershipRepository
                .findById(id)
                .orElseThrow(() -> new NoMembershipFoundException("could not find membership with id: " + id));
    }

    // TODO: Throw exception when empty or just return empty list?
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

    public Membership addMembership(Membership membership) throws MembershipAlreadyExistsException {
        if (membershipRepository.existsByUserIdAndProjectId(membership.getUserId(), membership.getProjectId())) {
            throw new MembershipAlreadyExistsException(
                    "membership for userId: " + membership.getUserId() +
                    " and projectId: " + membership.getProjectId() +
                    " already exists"
            );
        }
        return membershipRepository.save(membership);
    }

    public void deleteMembershipById(UUID id) throws NoMembershipFoundException {
        Long numOfDeletedMemberships = membershipRepository.removeById(id);

        if (numOfDeletedMemberships == 0) {
            throw new NoMembershipFoundException("could not delete because there was no membership with id: " + id);
        } else if (numOfDeletedMemberships > 1) {
            throw new ImpossibleException(
                    "!!! This should not happen. " +
                    "Multiple memberships were deleted when deleting membership with id: " + id
            );
        }
    }

    // TODO: Write query in repository for this?
    public Boolean areAllUsersProjectMembers(List<UUID> assigneeIds, UUID projectId) {
        HashSet<UUID> projectMemberIds =
                membershipRepository
                        .findByProjectId(projectId)
                        .stream()
                        .filter(membership -> membership.getState().equals(State.ACCEPTED))
                        .map(Membership::getUserId)
                        .collect(Collectors.toCollection(HashSet::new));

        return projectMemberIds.containsAll(assigneeIds);
    }

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

    public void deleteMembershipsByProjectId(UUID projectId) {
        membershipRepository.deleteByProjectId(projectId);
    }
}
