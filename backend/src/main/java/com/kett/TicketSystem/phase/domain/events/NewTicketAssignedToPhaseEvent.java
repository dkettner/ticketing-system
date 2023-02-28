package com.kett.TicketSystem.phase.domain.events;

import com.kett.TicketSystem.common.events.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class NewTicketAssignedToPhaseEvent extends DomainEvent {
    private final UUID phaseId;
    private final UUID ticketId;
    private final UUID projectId;

    public NewTicketAssignedToPhaseEvent(UUID phaseId, UUID ticketId, UUID projectId) {
        super();
        this.phaseId = phaseId;
        this.ticketId = ticketId;
        this.projectId = projectId;
    }
}
