package com.kett.TicketSystem.phase.domain.events;

import com.kett.TicketSystem.common.domainprimitives.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PhaseDeletedEvent extends DomainEvent {
    private final UUID phaseId;
    private final UUID projectId;

    public PhaseDeletedEvent(UUID phaseId, UUID projectId) {
        super();
        this.phaseId = phaseId;
        this.projectId = projectId;
    }
}
