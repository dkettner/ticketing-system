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

    @EventListener
    public void handleMembershipAcceptedEvent(MembershipAcceptedEvent membershipAcceptedEvent) {
        logger.trace(
                "accepted membership -> " +
                "membershipId:" + membershipAcceptedEvent.getMembershipId() +
                ", userId:" + membershipAcceptedEvent.getUserId() +
                ", projectId:" + membershipAcceptedEvent.getProjectId()
        );
    }
    @EventListener
    public void handleMembershipDeletedEvent(MembershipDeletedEvent membershipDeletedEvent) {
        logger.trace(
                "deleted membership -> " +
                "membershipId:" + membershipDeletedEvent.getMembershipId() +
                ", userId:" + membershipDeletedEvent.getUserId() +
                ", projectId:" + membershipDeletedEvent.getProjectId()
        );
    }
    @EventListener
    public void handleLastProjectMemberDeletedEvents(LastProjectMemberDeletedEvent lastProjectMemberDeletedEvent) {
        logger.trace(
                "deleted last membership -> " +
                "membershipId:" + lastProjectMemberDeletedEvent.getMembershipId() +
                ", userId:" + lastProjectMemberDeletedEvent.getUserId() +
                ", projectId:" + lastProjectMemberDeletedEvent.getProjectId()
        );
    }
    @EventListener
    public void handleUnacceptedProjectMembershipCreatedEvent(UnacceptedProjectMembershipCreatedEvent unacceptedProjectMembershipCreatedEvent) {
        logger.trace(
                "unaccepted membership -> " +
                "membershipId:" + unacceptedProjectMembershipCreatedEvent.getMembershipId() +
                ", userId:" + unacceptedProjectMembershipCreatedEvent.getInviteeId() + ", " +
                ", projectId:" + unacceptedProjectMembershipCreatedEvent.getProjectId()
        );
    }

    @EventListener
    public void handlePhaseCreatedEvent(PhaseCreatedEvent phaseCreatedEvent) {

    }
    @EventListener
    public void handlePhaseDeletedEvent(PhaseDeletedEvent phaseDeletedEvent) {

    }

    @EventListener
    public void handleProjectCreatedEvent(ProjectCreatedEvent projectCreatedEvent) {

    }
    @EventListener
    public void handleDefaultProjectCreatedEvent(DefaultProjectCreatedEvent defaultProjectCreatedEvent) {

    }
    @EventListener
    public void handleProjectDeletedEvent(ProjectDeletedEvent projectDeletedEvent) {

    }

    @EventListener
    public void handleUserCreatedEvent(UserCreatedEvent userCreatedEvent) {
        logger.trace(
                "created user -> " +
                "userId:" + userCreatedEvent.getUserId() +
                ", name:" + userCreatedEvent.getName() +
                ", email:" + userCreatedEvent.getEmailAddress()
        );
    }
    @EventListener
    public void handleUserDeletedEvent(UserDeletedEvent userDeletedEvent) {
        logger.trace(
                "deleted user -> " +
                        "userId:" + userDeletedEvent.getUserId() +
                        ", name:" + userDeletedEvent.getName()+
                        ", email:" + userDeletedEvent.getEmailAddress()
        );
    }
    @EventListener
    public void handleUserPatchedEvent(UserPatchedEvent userPatchedEvent) {
        logger.trace(
                "patched user -> " +
                        "userId:" + userPatchedEvent.getUserId() +
                        ", name:" + userPatchedEvent.getName()+
                        ", email:" + userPatchedEvent.getEmailAddress()
        );
    }
}
