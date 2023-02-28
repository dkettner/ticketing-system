package com.kett.TicketSystem.ticket.domain.events;

import com.kett.TicketSystem.common.domainprimitives.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class TicketCreatedEvent extends DomainEvent {
    private final UUID ticketId;
    private final UUID projectId;
    private final UUID userId;

    public TicketCreatedEvent(UUID ticketId, UUID projectId, UUID userId) {
        super();
        this.ticketId = ticketId;
        this.projectId = projectId;
        this.userId = userId;
    }
}
