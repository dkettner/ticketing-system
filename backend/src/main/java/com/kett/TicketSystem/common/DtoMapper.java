package com.kett.TicketSystem.common;

import com.kett.TicketSystem.membership.application.dto.MembershipPostDto;
import com.kett.TicketSystem.membership.application.dto.MembershipResponseDto;
import com.kett.TicketSystem.membership.domain.Membership;
import com.kett.TicketSystem.notification.application.dto.NotificationResponseDto;
import com.kett.TicketSystem.notification.domain.Notification;
import com.kett.TicketSystem.phase.application.dto.PhasePostDto;
import com.kett.TicketSystem.phase.application.dto.PhaseResponseDto;
import com.kett.TicketSystem.phase.domain.Phase;
import com.kett.TicketSystem.ticket.application.dto.TicketPostDto;
import com.kett.TicketSystem.ticket.application.dto.TicketResponseDto;
import com.kett.TicketSystem.project.application.dto.ProjectPostDto;
import com.kett.TicketSystem.project.application.dto.ProjectResponseDto;
import com.kett.TicketSystem.project.domain.Project;
import com.kett.TicketSystem.ticket.domain.Ticket;
import com.kett.TicketSystem.user.application.dto.UserPostDto;
import com.kett.TicketSystem.user.application.dto.UserResponseDto;
import com.kett.TicketSystem.user.domain.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class DtoMapper {
    private final ModelMapper modelMapper = new ModelMapper();

    public DtoMapper() {
        modelMapper.typeMap(Membership.class, MembershipResponseDto.class).addMappings(mapper -> {
            mapper.map(Membership::getId, MembershipResponseDto::setId);
            mapper.map(Membership::getProjectId, MembershipResponseDto::setProjectId);
            mapper.map(Membership::getUserId, MembershipResponseDto::setUserId);
            mapper.map(Membership::getRole, MembershipResponseDto::setRole);
            mapper.map(Membership::getState, MembershipResponseDto::setState);
        });
        modelMapper.typeMap(Notification.class, NotificationResponseDto.class).addMappings(mapper -> {
           mapper.map(Notification::getId, NotificationResponseDto::setId);
           mapper.map(Notification::getCreationTime, NotificationResponseDto::setCreationTime);
           mapper.map(Notification::getRecipientId, NotificationResponseDto::setRecipientId);
           mapper.map(Notification::getIsRead, NotificationResponseDto::setIsRead);
           mapper.map(Notification::getContent, NotificationResponseDto::setContent);
        });
        modelMapper.typeMap(Phase.class, PhaseResponseDto.class).addMappings(mapper -> {
            mapper.map(Phase::getId, PhaseResponseDto::setId);
            mapper.map(Phase::getProjectId, PhaseResponseDto::setProjectId);
            mapper.map(Phase::getName, PhaseResponseDto::setName);
            mapper.map(phase ->  {
                Phase previousPhase = phase.getPreviousPhase();
                return (previousPhase == null) ? null : previousPhase.getId();
            }, PhaseResponseDto::setPreviousPhaseId);
            mapper.map(phase ->  {
                Phase nextPhase = phase.getNextPhase();
                return (nextPhase == null) ? null : nextPhase.getId();
            }, PhaseResponseDto::setNextPhaseId);
            mapper.map(Phase::getTicketCount, PhaseResponseDto::setTicketCount);
        });
        modelMapper.typeMap(Project.class, ProjectResponseDto.class).addMappings(mapper -> {
            mapper.map(Project::getId, ProjectResponseDto::setId);
            mapper.map(Project::getName, ProjectResponseDto::setName);
            mapper.map(Project::getDescription, ProjectResponseDto::setDescription);
            mapper.map(Project::getCreationTime, ProjectResponseDto::setCreationTime);
        });
        modelMapper.typeMap(Ticket.class, TicketResponseDto.class).addMappings(mapper -> {
            mapper.map(Ticket::getId, TicketResponseDto::setId);
            mapper.map(Ticket::getTitle, TicketResponseDto::setTitle);
            mapper.map(Ticket::getDescription, TicketResponseDto::setDescription);
            mapper.map(Ticket::getCreationTime, TicketResponseDto::setCreationTime);
            mapper.map(Ticket::getDueTime, TicketResponseDto::setDueTime);
            mapper.map(Ticket::getPhaseId, TicketResponseDto:: setPhaseId);
            mapper.map(Ticket::getProjectId, TicketResponseDto::setProjectId);
            mapper.map(Ticket::getAssigneeIds, TicketResponseDto::setAssigneeIds);
        });
        modelMapper.typeMap(User.class, UserResponseDto.class).addMappings(mapper -> {
            mapper.map(User::getId, UserResponseDto::setId);
            mapper.map(User::getName, UserResponseDto::setName);
            mapper.map(user -> user.getEmail().toString(), UserResponseDto::setEmail);
        });
    }


    // membership

    public MembershipResponseDto mapMembershipToMembershipResponseDto(Membership membership) {
        return modelMapper.map(membership, MembershipResponseDto.class);
    }

    public List<MembershipResponseDto> mapMembershipListToMembershipResponseDtoList(List<Membership> memberships) {
        return memberships
                .stream()
                .map(membership -> modelMapper.map(membership, MembershipResponseDto.class))
                .toList();
    }

    public Membership mapMembershipPostDtoToMembership(MembershipPostDto membershipPostDto) {
        return new Membership(
                membershipPostDto.getProjectId(),
                membershipPostDto.getUserId(),
                membershipPostDto.getRole()
        );
    }


    // notification

    public NotificationResponseDto mapNotificationToNotificationResponseDto(Notification notification) {
        return modelMapper.map(notification, NotificationResponseDto.class);
    }

    public List<NotificationResponseDto> mapNotificationListToNotificationResponseDtoList(List<Notification> notifications) {
        return notifications
                .stream()
                .map(notification -> modelMapper.map(notification, NotificationResponseDto.class))
                .toList();
    }


    // phase

    public PhaseResponseDto mapPhaseToPhaseResponseDto(Phase phase) {
        return modelMapper.map(phase, PhaseResponseDto.class);
    }

    public List<PhaseResponseDto> mapPhaseListToPhaseResponseDtoList(List<Phase> phases) {
        return phases
                .stream()
                .map(phase -> modelMapper.map(phase, PhaseResponseDto.class))
                .toList();
    }

    public Phase mapPhasePostDtoToPhase(PhasePostDto phasePostDto) {
        return new Phase(
                phasePostDto.getProjectId(),
                phasePostDto.getName(),
                null,
                null
        );
    }


    // project

    public ProjectResponseDto mapProjectToProjectResponseDto(Project project) {
        return modelMapper.map(project, ProjectResponseDto.class);
    }

    public Project mapProjectPostDtoToProject(ProjectPostDto projectPostDto) {
        return new Project(projectPostDto.getName(), projectPostDto.getDescription());
    }


    // ticket

    public TicketResponseDto mapTicketToTicketResponseDto(Ticket ticket) {
        return modelMapper.map(ticket, TicketResponseDto.class);
    }

    public List<TicketResponseDto> mapTicketListToTicketResponseDtoList(List<Ticket> tickets) {
        return tickets
                .stream()
                .map(ticket -> modelMapper.map(ticket, TicketResponseDto.class))
                .toList();
    }

    public Ticket mapTicketPostDtoToTicket(TicketPostDto ticketPostDto, UUID phaseId) {
        return new Ticket(
                ticketPostDto.getTitle(),
                ticketPostDto.getDescription(),
                ticketPostDto.getDueTime(),
                ticketPostDto.getProjectId(),
                phaseId,
                ticketPostDto.getAssigneeIds()
        );
    }


    // user

    public UserResponseDto mapUserToUserResponseDto(User user) {
        return modelMapper.map(user, UserResponseDto.class);
    }

    public User mapUserPostDtoToUser(UserPostDto userPostDto) {
        return new User(
                userPostDto.getName(),
                userPostDto.getEmail(),
                userPostDto.getPassword()
        );
    }
}
