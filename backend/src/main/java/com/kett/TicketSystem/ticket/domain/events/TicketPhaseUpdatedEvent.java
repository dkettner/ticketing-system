package com.kett.TicketSystem.ticket.domain.events;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class TicketPhaseUpdatedEvent{
    private final UUID id;
    private final LocalDateTime timeStamp;
    private final UUID ticketId;
    private final UUID oldPhaseId;
    private final UUID newPhaseId;
    private final UUID projectId;
    private final UUID userId;

    public TicketPhaseUpdatedEvent(UUID ticketId, UUID oldPhaseId, UUID projectId, UUID userId, UUID newPhaseId) {
        this.id = UUID.randomUUID();
        this.timeStamp = LocalDateTime.now();
        this.ticketId = ticketId;
        this.projectId = projectId;
        this.userId = userId;
        this.oldPhaseId = oldPhaseId;
        this.newPhaseId = newPhaseId;
    }
}
