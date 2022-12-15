package com.kett.TicketSystem.ticket.domain.events;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class TicketPhaseUpdatedEvent{
    private final UUID id;
    private final LocalDateTime timeStamp;
    private final UUID ticketId;
    private final UUID projectId;
    private final UUID oldPhaseId;
    private final UUID newPhaseId;

    public TicketPhaseUpdatedEvent(UUID ticketId, UUID projectId, UUID oldPhaseId, UUID newPhaseId) {
        this.id = UUID.randomUUID();
        this.timeStamp = LocalDateTime.now();
        this.ticketId = ticketId;
        this.projectId = projectId;
        this.oldPhaseId = oldPhaseId;
        this.newPhaseId = newPhaseId;
    }
}
