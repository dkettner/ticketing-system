package com.kett.TicketSystem.common.logging;

import com.kett.TicketSystem.membership.domain.events.LastProjectMemberDeletedEvent;
import com.kett.TicketSystem.membership.domain.events.MembershipAcceptedEvent;
import com.kett.TicketSystem.membership.domain.events.MembershipDeletedEvent;
import com.kett.TicketSystem.membership.domain.events.UnacceptedProjectMembershipCreatedEvent;
import com.kett.TicketSystem.phase.domain.events.PhaseCreatedEvent;
import com.kett.TicketSystem.phase.domain.events.PhaseDeletedEvent;
import com.kett.TicketSystem.project.domain.events.DefaultProjectCreatedEvent;
import com.kett.TicketSystem.project.domain.events.ProjectCreatedEvent;
import com.kett.TicketSystem.project.domain.events.ProjectDeletedEvent;
import com.kett.TicketSystem.ticket.domain.events.TicketCreatedEvent;
import com.kett.TicketSystem.user.domain.events.UserCreatedEvent;
import com.kett.TicketSystem.user.domain.events.UserDeletedEvent;
import com.kett.TicketSystem.user.domain.events.UserPatchedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EventLogger {
    Logger logger = LoggerFactory.getLogger(EventLogger.class);


    // membership

    @EventListener
    public void handleMembershipAcceptedEvent(MembershipAcceptedEvent membershipAcceptedEvent) {
        logger.trace(
                "membership accepted -> " +
                "membershipId:" + membershipAcceptedEvent.getMembershipId() +
                ", userId:" + membershipAcceptedEvent.getUserId() +
                ", projectId:" + membershipAcceptedEvent.getProjectId()
        );
    }
    @EventListener
    public void handleMembershipDeletedEvent(MembershipDeletedEvent membershipDeletedEvent) {
        logger.trace(
                "membership deleted -> " +
                "membershipId:" + membershipDeletedEvent.getMembershipId() +
                ", userId:" + membershipDeletedEvent.getUserId() +
                ", projectId:" + membershipDeletedEvent.getProjectId()
        );
    }
    @EventListener
    public void handleLastProjectMemberDeletedEvents(LastProjectMemberDeletedEvent lastProjectMemberDeletedEvent) {
        logger.trace(
                "last membership deleted -> " +
                "membershipId:" + lastProjectMemberDeletedEvent.getMembershipId() +
                ", userId:" + lastProjectMemberDeletedEvent.getUserId() +
                ", projectId:" + lastProjectMemberDeletedEvent.getProjectId()
        );
    }
    @EventListener
    public void handleUnacceptedProjectMembershipCreatedEvent(UnacceptedProjectMembershipCreatedEvent unacceptedProjectMembershipCreatedEvent) {
        logger.trace(
                "unaccepted membership created -> " +
                "membershipId:" + unacceptedProjectMembershipCreatedEvent.getMembershipId() +
                ", userId:" + unacceptedProjectMembershipCreatedEvent.getInviteeId() + ", " +
                ", projectId:" + unacceptedProjectMembershipCreatedEvent.getProjectId()
        );
    }


    // phase

    @EventListener
    public void handlePhaseCreatedEvent(PhaseCreatedEvent phaseCreatedEvent) {

    }
    @EventListener
    public void handlePhaseDeletedEvent(PhaseDeletedEvent phaseDeletedEvent) {

    }


    // project

    @EventListener
    public void handleProjectCreatedEvent(ProjectCreatedEvent projectCreatedEvent) {

    }
    @EventListener
    public void handleDefaultProjectCreatedEvent(DefaultProjectCreatedEvent defaultProjectCreatedEvent) {

    }
    @EventListener
    public void handleProjectDeletedEvent(ProjectDeletedEvent projectDeletedEvent) {

    }


    // ticket

    @EventListener
    public void handleTicketCreatedEvent(TicketCreatedEvent ticketCreatedEvent) {
        logger.trace(
                "ticket created -> " +
                "ticketId:" + ticketCreatedEvent.getTicketId() +
                ", userId:" + ticketCreatedEvent.getUserId() + ", " +
                "projectId:" + ticketCreatedEvent.getProjectId()
        );
    }


    // user

    @EventListener
    public void handleUserCreatedEvent(UserCreatedEvent userCreatedEvent) {
        logger.trace(
                "user created -> " +
                "userId:" + userCreatedEvent.getUserId() +
                ", name:" + userCreatedEvent.getName() +
                ", email:" + userCreatedEvent.getEmailAddress()
        );
    }
    @EventListener
    public void handleUserDeletedEvent(UserDeletedEvent userDeletedEvent) {
        logger.trace(
                "user deleted -> " +
                "userId:" + userDeletedEvent.getUserId() +
                ", name:" + userDeletedEvent.getName()+
                ", email:" + userDeletedEvent.getEmailAddress()
        );
    }
    @EventListener
    public void handleUserPatchedEvent(UserPatchedEvent userPatchedEvent) {
        logger.trace(
                "user patched -> " +
                "userId:" + userPatchedEvent.getUserId() +
                ", name:" + userPatchedEvent.getName()+
                ", email:" + userPatchedEvent.getEmailAddress()
        );
    }
}
