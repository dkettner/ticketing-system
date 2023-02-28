package com.kett.TicketSystem.common.domainprimitives;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public abstract class DomainEvent {
    protected final UUID id;
    protected final LocalDateTime timeStamp;

    protected DomainEvent() {
        this.id = UUID.randomUUID();
        this.timeStamp = LocalDateTime.now();
    }
}
