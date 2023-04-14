package com.kett.TicketSystem.util;

import com.kett.TicketSystem.membership.domain.events.LastProjectMemberDeletedEvent;
import com.kett.TicketSystem.membership.domain.events.MembershipAcceptedEvent;
import com.kett.TicketSystem.membership.domain.events.MembershipDeletedEvent;
import com.kett.TicketSystem.membership.domain.events.UnacceptedProjectMembershipCreatedEvent;
import com.kett.TicketSystem.phase.domain.events.PhaseCreatedEvent;
import com.kett.TicketSystem.phase.domain.events.PhaseDeletedEvent;
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
    Stack<UnacceptedProjectMembershipCreatedEvent> unacceptedProjectMembershipCreatedEvents = new Stack<>();
    Stack<MembershipAcceptedEvent> membershipAcceptedEvents = new Stack<>();
    Stack<MembershipDeletedEvent> membershipDeletedEvents = new Stack<>();
    Stack<LastProjectMemberDeletedEvent> lastProjectMemberDeletedEvents = new Stack<>();

    Stack<PhaseCreatedEvent> phaseCreatedEvents = new Stack<>();
    Stack<PhaseDeletedEvent> phaseDeletedEvents = new Stack<>();

    Stack<ProjectCreatedEvent> projectCreatedEvents = new Stack<>();
    Stack<DefaultProjectCreatedEvent> defaultProjectCreatedEvents = new Stack<>();
    Stack<ProjectDeletedEvent> projectDeletedEvents = new Stack<>();

    Stack<UserCreatedEvent> userCreatedEvents = new Stack<>();
    Stack<UserPatchedEvent> userPatchedEvents = new Stack<>();
    Stack<UserDeletedEvent> userDeletedEvents = new Stack<>();

    public Optional<UnacceptedProjectMembershipCreatedEvent> getLatestUnacceptedProjectMembershipCreatedEvent() {
        Optional<UnacceptedProjectMembershipCreatedEvent> event = Optional.empty();
        if (!unacceptedProjectMembershipCreatedEvents.isEmpty()) {
            event = Optional.of(unacceptedProjectMembershipCreatedEvents.pop());
        }
        return event;
    }
    public Optional<MembershipAcceptedEvent> getLatestMembershipAcceptedEvent() {
        Optional<MembershipAcceptedEvent> event = Optional.empty();
        if (!membershipAcceptedEvents.isEmpty()) {
            event = Optional.of(membershipAcceptedEvents.pop());
        }
        return event;
    }
    public Optional<MembershipDeletedEvent> getLatestMembershipDeletedEvent() {
        Optional<MembershipDeletedEvent> event = Optional.empty();
        if (!membershipDeletedEvents.isEmpty()) {
            event = Optional.of(membershipDeletedEvents.pop());
        }
        return event;
    }
    public Optional<LastProjectMemberDeletedEvent> getLatestLastProjectMembershipDeletedEvent() {
        Optional<LastProjectMemberDeletedEvent> event = Optional.empty();
        if (!lastProjectMemberDeletedEvents.isEmpty()) {
            event = Optional.of(lastProjectMemberDeletedEvents.pop());
        }
        return event;
    }

    public Optional<PhaseCreatedEvent> getLatestPhaseCreatedEvent() {
        Optional<PhaseCreatedEvent> event = Optional.empty();
        if (!phaseCreatedEvents.isEmpty()) {
            event = Optional.of(phaseCreatedEvents.pop());
        }
        return event;
    }
    public Optional<PhaseDeletedEvent> getLatestPhaseDeletedEvent() {
        Optional<PhaseDeletedEvent> event = Optional.empty();
        if (!phaseDeletedEvents.isEmpty()) {
            event = Optional.of(phaseDeletedEvents.pop());
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
        if (!projectDeletedEvents.isEmpty()) {
            event = Optional.of(projectDeletedEvents.pop());
        }
        return event;
    }

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

    public void deleteAllEvents() {
        unacceptedProjectMembershipCreatedEvents.clear();
        membershipAcceptedEvents.clear();
        membershipDeletedEvents.clear();
        lastProjectMemberDeletedEvents.clear();

        phaseCreatedEvents.clear();
        phaseDeletedEvents.clear();

        projectCreatedEvents.clear();
        defaultProjectCreatedEvents.clear();
        projectDeletedEvents.clear();

        userCreatedEvents.clear();
        userPatchedEvents.clear();
        userDeletedEvents.clear();
    }

    @EventListener
    public void handleUnacceptedProjectMembershipCreatedEvent(
            UnacceptedProjectMembershipCreatedEvent unacceptedProjectMembershipCreatedEvent
    ) {
        unacceptedProjectMembershipCreatedEvents.push(unacceptedProjectMembershipCreatedEvent);
    }
    @EventListener
    public void handleMembershipAcceptedEvent(MembershipAcceptedEvent membershipAcceptedEvent) {
        membershipAcceptedEvents.push(membershipAcceptedEvent);
    }
    @EventListener
    public void handleMembershipDeletedEvent(MembershipDeletedEvent membershipDeletedEvent) {
        membershipDeletedEvents.push(membershipDeletedEvent);
    }
    @EventListener
    public void handleLastProjectMemberDeletedEvents(LastProjectMemberDeletedEvent lastProjectMemberDeletedEvent) {
        lastProjectMemberDeletedEvents.push(lastProjectMemberDeletedEvent);
    }

    @EventListener
    public void handlePhaseCreatedEvent(PhaseCreatedEvent phaseCreatedEvent) {
        phaseCreatedEvents.push(phaseCreatedEvent);
    }
    @EventListener
    public void handlePhaseDeletedEvent(PhaseDeletedEvent phaseDeletedEvent) {
        phaseDeletedEvents.push(phaseDeletedEvent);
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
