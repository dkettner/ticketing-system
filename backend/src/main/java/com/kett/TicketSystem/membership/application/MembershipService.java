package com.kett.TicketSystem.membership.application;

import com.kett.TicketSystem.membership.domain.Membership;
import com.kett.TicketSystem.membership.domain.exceptions.MembershipAlreadyExistsException;
import com.kett.TicketSystem.membership.domain.exceptions.NoMembershipFoundException;
import com.kett.TicketSystem.membership.repository.MembershipRepository;
import com.kett.TicketSystem.project.domain.exceptions.ImpossibleException;
import com.kett.TicketSystem.project.domain.exceptions.NoProjectFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MembershipService {
    private final MembershipRepository membershipRepository;

    @Autowired
    public MembershipService(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    public Membership getMembershipById(UUID id) throws NoProjectFoundException {
        return membershipRepository
                .findById(id)
                .orElseThrow(() -> new NoProjectFoundException("could not find membership with id: " + id));
    }

    // TODO: Throw exception when empty or just return empty list?
    public List<Membership> getMembershipsByUserId(UUID userId) {
        List<Membership> memberships =  membershipRepository.findByUserId(userId);
        if (memberships.isEmpty()) {
            throw new NoMembershipFoundException("could not find memberships with userId: " + userId);
        }
        return memberships;
    }

    public List<Membership> getMembershipsByProjectId(UUID projectId) {
        List<Membership> memberships =  membershipRepository.findByProjectId(projectId);
        if (memberships.isEmpty()) {
            throw new NoMembershipFoundException("could not find memberships with projectId: " + projectId);
        }
        return memberships;
    }

    public Membership addMembership(Membership membership) {
        if (membershipRepository.existsByUserIdAndProjectId(membership.getUserId(), membership.getProjectId())) {
            throw new MembershipAlreadyExistsException(
                    "membership for userId: " + membership.getUserId() +
                    " and projectId: " + membership.getProjectId() +
                    " already exists"
            );
        }
        return membershipRepository.save(membership);
    }

    public void deleteMembershipById(UUID id) {
        Long numOfDeletedMemberships = membershipRepository.removeById(id);

        if (numOfDeletedMemberships == 0) {
            throw new NoMembershipFoundException("could not delete because there was no membership with id: " + id);
        } else if (numOfDeletedMemberships > 1) {
            throw new ImpossibleException(
                    "!!! This should not happen. " +
                            "Multiple memberships were deleted when deleting project with id: " + id
            );
        }
    }
}
