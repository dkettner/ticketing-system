package com.kett.TicketSystem.user.domain.events;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class UserDeletedEvent {
    private final UUID id;
    private final LocalDateTime timeStamp;
    private final UUID userId;

    public UserDeletedEvent(UUID userId) {
        this.id = UUID.randomUUID();
        this.timeStamp = LocalDateTime.now();
        this.userId = userId;
    }
}
