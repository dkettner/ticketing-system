package com.kett.TicketSystem.ticket.domain.events;

import com.kett.TicketSystem.common.domainprimitives.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class TicketDeletedEvent extends DomainEvent {
    private final UUID ticketId;
    private final UUID projectId;
    private final UUID phaseId;

    public TicketDeletedEvent(UUID ticketId, UUID projectId, UUID phaseId) {
        super();
        this.ticketId = ticketId;
        this.projectId = projectId;
        this.phaseId = phaseId;
    }
}
