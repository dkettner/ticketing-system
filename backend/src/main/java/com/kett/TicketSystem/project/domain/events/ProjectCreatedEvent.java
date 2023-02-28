package com.kett.TicketSystem.project.domain.events;

import com.kett.TicketSystem.common.domainprimitives.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ProjectCreatedEvent extends DomainEvent {
    private final UUID projectId;
    private final UUID userId;

    public ProjectCreatedEvent(UUID projectId, UUID userId) {
        super();
        this.projectId = projectId;
        this.userId = userId;
    }
}
