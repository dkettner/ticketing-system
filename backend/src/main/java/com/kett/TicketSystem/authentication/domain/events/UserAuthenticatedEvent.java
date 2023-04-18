package com.kett.TicketSystem.authentication.domain.events;

import com.kett.TicketSystem.common.domainprimitives.DomainEvent;
import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import lombok.Getter;


@Getter
public class UserAuthenticatedEvent extends DomainEvent {
    private final EmailAddress emailAddress;

    public UserAuthenticatedEvent(EmailAddress emailAddress) {
        super();
        this.emailAddress = emailAddress;
    }
}
