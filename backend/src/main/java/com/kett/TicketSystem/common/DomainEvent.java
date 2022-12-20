package com.kett.TicketSystem.common;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public abstract class DomainEvent {
    private final UUID id;
    private final LocalDateTime timeStamp;

    protected DomainEvent() {
        this.id = UUID.randomUUID();
        this.timeStamp = LocalDateTime.now();
    }
}