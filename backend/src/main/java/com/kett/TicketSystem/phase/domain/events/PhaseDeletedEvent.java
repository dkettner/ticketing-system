package com.kett.TicketSystem.phase.domain.events;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class PhaseDeletedEvent {
    private final UUID id;
    private final LocalDateTime timeStamp;
    private final UUID phaseId;
    private final UUID projectId;

    public PhaseDeletedEvent(UUID phaseId, UUID projectId) {
        this.id = UUID.randomUUID();
        this.timeStamp = LocalDateTime.now();
        this.phaseId = phaseId;
        this.projectId = projectId;
    }
}
