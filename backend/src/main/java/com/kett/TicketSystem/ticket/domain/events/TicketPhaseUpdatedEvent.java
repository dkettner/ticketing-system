package com.kett.TicketSystem.ticket.domain.events;

import com.kett.TicketSystem.common.domainprimitives.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class TicketPhaseUpdatedEvent extends DomainEvent {
    private final UUID ticketId;
    private final UUID projectId;
    private final UUID oldPhaseId;
    private final UUID newPhaseId;

    public TicketPhaseUpdatedEvent(UUID ticketId, UUID projectId, UUID oldPhaseId, UUID newPhaseId) {
        super();
        this.ticketId = ticketId;
        this.projectId = projectId;
        this.oldPhaseId = oldPhaseId;
        this.newPhaseId = newPhaseId;
    }
}
