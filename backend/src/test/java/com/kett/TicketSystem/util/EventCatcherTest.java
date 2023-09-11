package com.kett.TicketSystem.util;

import com.kett.TicketSystem.membership.domain.events.MembershipAcceptedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles({ "test" })
public class EventCatcherTest {
    private final EventCatcher eventCatcher;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public EventCatcherTest(EventCatcher eventCatcher, ApplicationEventPublisher eventPublisher) {
        this.eventCatcher = eventCatcher;
        this.eventPublisher = eventPublisher;
    }

    @Test
    public void eventCatcherTest() {

        // should not get caught
        MembershipAcceptedEvent event0 = new MembershipAcceptedEvent(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        eventPublisher.publishEvent(event0);

        // start catching
        eventCatcher.catchEventOfType(MembershipAcceptedEvent.class);

        // no event of the specified type
        assertNull(eventCatcher.getEvent());
        assertNull(eventCatcher.getEvent());
        assertNull(eventCatcher.getEvent());

        // should get caught
        MembershipAcceptedEvent event1 = new MembershipAcceptedEvent(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        eventPublisher.publishEvent(event1);

        await().until(eventCatcher::hasCaughtEvent);    // fails if not caught within 10 seconds

        // test if it is the same event
        MembershipAcceptedEvent membershipAcceptedEvent = (MembershipAcceptedEvent) eventCatcher.getEvent();
        assertNotEquals(event0, membershipAcceptedEvent);
        assertEquals(event1, membershipAcceptedEvent);
    }
}
