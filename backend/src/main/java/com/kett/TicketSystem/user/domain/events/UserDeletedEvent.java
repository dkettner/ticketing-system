package com.kett.TicketSystem.user.domain.events;

import com.kett.TicketSystem.common.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UserDeletedEvent extends DomainEvent {
    private final UUID userId;

    public UserDeletedEvent(UUID userId) {
        super();
        this.userId = userId;
    }
}
