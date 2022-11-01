package com.kett.TicketSystem.membership.application;

import com.kett.TicketSystem.membership.domain.Membership;
import com.kett.TicketSystem.membership.repository.MembershipRepository;
import com.kett.TicketSystem.project.domain.exceptions.NoProjectFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
