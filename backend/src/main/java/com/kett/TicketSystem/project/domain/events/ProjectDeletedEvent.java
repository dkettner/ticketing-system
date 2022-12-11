package com.kett.TicketSystem.project.domain.events;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ProjectDeletedEvent {
    private final UUID id;
    private final LocalDateTime timeStamp;
    private final UUID projectId;

    public ProjectDeletedEvent(UUID projectId) {
        this.id = UUID.randomUUID();
        this.timeStamp = LocalDateTime.now();
        this.projectId = projectId;
    }
}
