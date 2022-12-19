package com.kett.TicketSystem.membership.domain.events;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class MembershipAcceptedEvent {
    private final UUID id;
    private final LocalDateTime timeStamp;
    private final UUID membershipId;
    private final UUID projectId;
    private final UUID userId;

    public MembershipAcceptedEvent(UUID membershipId, UUID projectId, UUID userId) {
        this.id = UUID.randomUUID();
        this.timeStamp = LocalDateTime.now();
        this.membershipId = membershipId;
        this.projectId = projectId;
        this.userId = userId;
    }
}
