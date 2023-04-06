package com.kett.TicketSystem.util;

import com.kett.TicketSystem.user.domain.events.UserCreatedEvent;
import com.kett.TicketSystem.user.domain.events.UserDeletedEvent;
import com.kett.TicketSystem.user.domain.events.UserPatchedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Stack;

@Service
public class DummyEventListener {
    Stack<UserCreatedEvent> userCreatedEvents = new Stack<>();
    Stack<UserDeletedEvent> userDeletedEvents = new Stack<>();
    Stack<UserPatchedEvent> userPatchedEvents = new Stack<>();

    public Optional<UserCreatedEvent> getLatestUserCreatedEvent() {
        Optional<UserCreatedEvent> event = Optional.empty();
        if (!userCreatedEvents.isEmpty()) {
            event = Optional.of(userCreatedEvents.pop());
        }
        return event;
    }

    public Optional<UserDeletedEvent> getLatestUserDeletedEvent() {
        Optional<UserDeletedEvent> event = Optional.empty();
        if (!userDeletedEvents.isEmpty()) {
            event = Optional.of(userDeletedEvents.pop());
        }
        return event;
    }

    public void deleteAllEvents() {
        userCreatedEvents.clear();
        userDeletedEvents.clear();
    }

    public Optional<UserPatchedEvent> getLatestUserPatchedEvent() {
        Optional<UserPatchedEvent> event = Optional.empty();
        if (!userPatchedEvents.isEmpty()) {
            event = Optional.of(userPatchedEvents.pop());
        }
        return event;
    }

    @EventListener
    public void handleUserCreatedEvent(UserCreatedEvent userCreatedEvent) {
        userCreatedEvents.push(userCreatedEvent);
    }

    @EventListener
    public void handleUserDeletedEvent(UserDeletedEvent userDeletedEvent) {
        userDeletedEvents.push(userDeletedEvent);
    }

    @EventListener
    public void handleUserPatchedEvent(UserPatchedEvent userPatchedEvent) {
        userPatchedEvents.push(userPatchedEvent);
    }
}
