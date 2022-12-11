package com.kett.TicketSystem.user.domain.events;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class UserCreatedEvent {
    private final UUID id;
    private final LocalDateTime timeStamp;
    private final UUID userId;

    public UserCreatedEvent(UUID userId) {
        this.id = UUID.randomUUID();
        this.timeStamp = LocalDateTime.now();
        this.userId = userId;
    }
}
