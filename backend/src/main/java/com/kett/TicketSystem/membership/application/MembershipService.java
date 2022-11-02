package com.kett.TicketSystem.membership.application;

import com.kett.TicketSystem.membership.domain.Membership;
import com.kett.TicketSystem.membership.domain.exceptions.NoMembershipFoundException;
import com.kett.TicketSystem.membership.repository.MembershipRepository;
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
}
