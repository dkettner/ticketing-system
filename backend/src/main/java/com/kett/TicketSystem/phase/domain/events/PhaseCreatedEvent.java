package com.kett.TicketSystem.phase.domain.events;

import com.kett.TicketSystem.common.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PhaseCreatedEvent extends DomainEvent {
    private final UUID phaseId;
    private final UUID projectId;

    public PhaseCreatedEvent(UUID phaseId, UUID projectId) {
        super();
        this.phaseId = phaseId;
        this.projectId = projectId;
    }
}
