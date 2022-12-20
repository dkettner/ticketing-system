package com.kett.TicketSystem.membership.domain.events;

import com.kett.TicketSystem.common.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class LastProjectMemberDeletedEvent extends DomainEvent {
    private final UUID userId;
    private final UUID projectId;

    public LastProjectMemberDeletedEvent(UUID userId, UUID projectId) {
        super();
        this.userId = userId;
        this.projectId = projectId;
    }
}
