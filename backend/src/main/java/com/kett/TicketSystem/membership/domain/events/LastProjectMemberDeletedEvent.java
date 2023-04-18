package com.kett.TicketSystem.membership.domain.events;

import com.kett.TicketSystem.common.domainprimitives.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class LastProjectMemberDeletedEvent extends DomainEvent {
    private final UUID membershipId;
    private final UUID userId;
    private final UUID projectId;

    public LastProjectMemberDeletedEvent(UUID membershipId, UUID userId, UUID projectId) {
        super();
        this.membershipId = membershipId;
        this.userId = userId;
        this.projectId = projectId;
    }
}
