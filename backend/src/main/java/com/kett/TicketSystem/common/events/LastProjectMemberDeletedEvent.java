package com.kett.TicketSystem.common.events;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class LastProjectMemberDeletedEvent {
    private final UUID id;
    private final LocalDateTime timeStamp;
    private final UUID userId;
    private final UUID projectId;

    public LastProjectMemberDeletedEvent(UUID userId, UUID projectId) {
        this.id = UUID.randomUUID();
        this.timeStamp = LocalDateTime.now();
        this.userId = userId;
        this.projectId = projectId;
    }
}
