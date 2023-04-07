package com.kett.TicketSystem.util;

import com.kett.TicketSystem.project.domain.events.DefaultProjectCreatedEvent;
import com.kett.TicketSystem.project.domain.events.ProjectCreatedEvent;
import com.kett.TicketSystem.project.domain.events.ProjectDeletedEvent;
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
    Stack<UserPatchedEvent> userPatchedEvents = new Stack<>();
    Stack<UserDeletedEvent> userDeletedEvents = new Stack<>();

    Stack<ProjectCreatedEvent> projectCreatedEvents = new Stack<>();
    Stack<DefaultProjectCreatedEvent> defaultProjectCreatedEvents = new Stack<>();
    Stack<ProjectDeletedEvent> projectDeletedEvents = new Stack<>();

    public Optional<UserCreatedEvent> getLatestUserCreatedEvent() {
        Optional<UserCreatedEvent> event = Optional.empty();
        if (!userCreatedEvents.isEmpty()) {
            event = Optional.of(userCreatedEvents.pop());
        }
        return event;
    }
    public Optional<UserPatchedEvent> getLatestUserPatchedEvent() {
        Optional<UserPatchedEvent> event = Optional.empty();
        if (!userPatchedEvents.isEmpty()) {
            event = Optional.of(userPatchedEvents.pop());
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

    public Optional<ProjectCreatedEvent> getLatestProjectCreatedEvent() {
        Optional<ProjectCreatedEvent> event = Optional.empty();
        if (!projectCreatedEvents.isEmpty()) {
            event = Optional.of(projectCreatedEvents.pop());
        }
        return event;
    }
    public Optional<DefaultProjectCreatedEvent> getLatestDefaultProjectCreatedEvent() {
        Optional<DefaultProjectCreatedEvent> event = Optional.empty();
        if (!defaultProjectCreatedEvents.isEmpty()) {
            event = Optional.of(defaultProjectCreatedEvents.pop());
        }
        return event;
    }
    public Optional<ProjectDeletedEvent> getLatestProjectDeletedEvent() {
        Optional<ProjectDeletedEvent> event = Optional.empty();
        if (!projectCreatedEvents.isEmpty()) {
            event = Optional.of(projectDeletedEvents.pop());
        }
        return event;
    }

    public void deleteAllEvents() {
        userCreatedEvents.clear();
        userPatchedEvents.clear();
        userDeletedEvents.clear();

        projectDeletedEvents.clear();
        defaultProjectCreatedEvents.clear();
        projectDeletedEvents.clear();
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

    @EventListener
    public void handleProjectCreatedEvent(ProjectCreatedEvent projectCreatedEvent) {
        projectCreatedEvents.push(projectCreatedEvent);
    }
    @EventListener
    public void handleDefaultProjectCreatedEvent(DefaultProjectCreatedEvent defaultProjectCreatedEvent) {
        defaultProjectCreatedEvents.push(defaultProjectCreatedEvent);
    }
    @EventListener
    public void handleProjectDeletedEvent(ProjectDeletedEvent projectDeletedEvent) {
        projectDeletedEvents.push(projectDeletedEvent);
    }
}
