package com.kett.TicketSystem.ticket.domain.events;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class TicketDeletedEvent {
    private final UUID id;
    private final LocalDateTime timeStamp;
    private final UUID ticketId;
    private final UUID projectId;
    private final UUID phaseId;

    public TicketDeletedEvent(UUID ticketId, UUID projectId, UUID phaseId) {
        this.id = UUID.randomUUID();
        this.timeStamp = LocalDateTime.now();
        this.ticketId = ticketId;
        this.projectId = projectId;
        this.phaseId = phaseId;
    }
}
