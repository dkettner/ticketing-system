package com.kett.TicketSystem.ticket.domain.events;

import com.kett.TicketSystem.common.DomainEvent;
import lombok.Getter;

import java.util.UUID;


@Getter
public class TicketUnassignedEvent extends DomainEvent {
    private final UUID ticketId;
    private final UUID projectId;
    private final UUID userId;

    public TicketUnassignedEvent(UUID ticketId, UUID projectId, UUID userId) {
        super();
        this.ticketId = ticketId;
        this.projectId = projectId;
        this.userId = userId;
    }
}
