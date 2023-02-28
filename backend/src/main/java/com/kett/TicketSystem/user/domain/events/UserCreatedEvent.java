package com.kett.TicketSystem.user.domain.events;

import com.kett.TicketSystem.common.events.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UserCreatedEvent extends DomainEvent {
    private final UUID userId;

    public UserCreatedEvent(UUID userId) {
        super();
        this.userId = userId;
    }
}
