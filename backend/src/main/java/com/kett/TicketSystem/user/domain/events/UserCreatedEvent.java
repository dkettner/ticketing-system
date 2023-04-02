package com.kett.TicketSystem.user.domain.events;

import com.kett.TicketSystem.common.domainprimitives.DomainEvent;
import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UserCreatedEvent extends DomainEvent {
    private final UUID userId;
    private final String name;
    private final EmailAddress emailAddress;

    public UserCreatedEvent(UUID userId, String name, EmailAddress emailAddress) {
        super();
        this.userId = userId;
        this.name = name;
        this.emailAddress = emailAddress;
    }
}
