package com.kett.TicketSystem.ticket.domain.events;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class TicketCreatedEvent {
    private final UUID id;
    private final LocalDateTime timeStamp;
    private final UUID ticketId;
    private final UUID projectId;
    private final UUID userId;

    public TicketCreatedEvent(UUID ticketId, UUID projectId, UUID userId) {
        this.id = UUID.randomUUID();
        this.timeStamp = LocalDateTime.now();
        this.ticketId = ticketId;
        this.projectId = projectId;
        this.userId = userId;
    }
}
