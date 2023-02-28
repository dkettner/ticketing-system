package com.kett.TicketSystem.project.domain.events;

import com.kett.TicketSystem.common.domainprimitives.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ProjectDeletedEvent extends DomainEvent {
    private final UUID projectId;

    public ProjectDeletedEvent(UUID projectId) {
        super();
        this.projectId = projectId;
    }
}
