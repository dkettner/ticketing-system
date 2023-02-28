package com.kett.TicketSystem.membership.domain.events;

import com.kett.TicketSystem.common.domainprimitives.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class MembershipDeletedEvent extends DomainEvent {
    private final UUID membershipId;
    private final UUID projectId;
    private final UUID userId;

    public MembershipDeletedEvent(UUID membershipId, UUID projectId, UUID userId) {
        super();
        this.membershipId = membershipId;
        this.projectId = projectId;
        this.userId = userId;
    }
}
