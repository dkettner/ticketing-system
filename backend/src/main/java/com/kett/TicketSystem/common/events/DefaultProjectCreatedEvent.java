package com.kett.TicketSystem.common.events;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class DefaultProjectCreatedEvent {
    private final UUID id;
    private final LocalDateTime timeStamp;
    private final UUID projectId;
    private final UUID userId;

    public DefaultProjectCreatedEvent(UUID projectId, UUID userId) {
        this.id = UUID.randomUUID();
        this.timeStamp = LocalDateTime.now();
        this.projectId = projectId;
        this.userId = userId;
    }
}
