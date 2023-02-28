package com.kett.TicketSystem.project.domain.events;

import com.kett.TicketSystem.common.domainprimitives.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DefaultProjectCreatedEvent extends DomainEvent {
    private final UUID projectId;
    private final UUID userId;

    public DefaultProjectCreatedEvent(UUID projectId, UUID userId) {
        super();
        this.projectId = projectId;
        this.userId = userId;
    }
}
