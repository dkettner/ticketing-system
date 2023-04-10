package com.kett.TicketSystem.membership.domain.events;

import com.kett.TicketSystem.common.domainprimitives.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UnacceptedProjectMembershipCreatedEvent extends DomainEvent {
    private final UUID membershipId;
    private final UUID inviteeId;
    private final UUID projectId;

    public UnacceptedProjectMembershipCreatedEvent(UUID membershipId, UUID inviteeId, UUID projectId) {
        super();
        this.membershipId = membershipId;
        this.inviteeId = inviteeId;
        this.projectId = projectId;
    }
}
