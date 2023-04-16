package com.kett.TicketSystem.application;

import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import com.kett.TicketSystem.membership.domain.MembershipDomainService;
import com.kett.TicketSystem.notification.application.NotificationService;
import com.kett.TicketSystem.notification.application.dto.NotificationPatchIsReadDto;
import com.kett.TicketSystem.notification.application.dto.NotificationResponseDto;
import com.kett.TicketSystem.notification.domain.Notification;
import com.kett.TicketSystem.phase.application.dto.PhasePatchNameDto;
import com.kett.TicketSystem.phase.application.dto.PhasePatchPositionDto;
import com.kett.TicketSystem.phase.application.dto.PhasePostDto;
import com.kett.TicketSystem.phase.application.dto.PhaseResponseDto;
import com.kett.TicketSystem.phase.domain.Phase;
import com.kett.TicketSystem.phase.domain.PhaseDomainService;
import com.kett.TicketSystem.project.application.ProjectService;
import com.kett.TicketSystem.project.application.dto.*;
import com.kett.TicketSystem.project.domain.Project;
import com.kett.TicketSystem.ticket.domain.TicketDomainService;
import com.kett.TicketSystem.ticket.application.dto.TicketPatchDto;
import com.kett.TicketSystem.ticket.application.dto.TicketPostDto;
import com.kett.TicketSystem.ticket.application.dto.TicketResponseDto;
import com.kett.TicketSystem.ticket.domain.Ticket;
import com.kett.TicketSystem.user.application.UserService;
import com.kett.TicketSystem.user.application.dto.UserPatchDto;
import com.kett.TicketSystem.user.application.dto.UserPostDto;
import com.kett.TicketSystem.user.application.dto.UserResponseDto;
import com.kett.TicketSystem.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TicketSystemService {
    private final NotificationService notificationService;
    private final PhaseDomainService phaseDomainService;
    private final ProjectService projectService;
    private final TicketDomainService ticketDomainService;
    private final UserService userService;
    private final DtoMapper dtoMapper;

    @Autowired
    public TicketSystemService (
            NotificationService notificationService,
            PhaseDomainService phaseDomainService,
            ProjectService projectService,
            TicketDomainService ticketDomainService,
            UserService userService,
            DtoMapper dtoMapper
    ) {
        this.notificationService = notificationService;
        this.phaseDomainService = phaseDomainService;
        this.projectService = projectService;
        this.ticketDomainService = ticketDomainService;
        this.userService = userService;
        this.dtoMapper = dtoMapper;
    }


    // notification
    @PreAuthorize("hasAuthority('ROLE_USER_'.concat(@notificationService.getGetRecipientIdByNotificationId(#id)))")
    public NotificationResponseDto getNotificationById(UUID id) {
        Notification notification = notificationService.getNotificationById(id);
        return dtoMapper.mapNotificationToNotificationResponseDto(notification);
    }

    @PreAuthorize("hasAuthority('ROLE_USER_'.concat(#recipientId))")
    public List<NotificationResponseDto> getNotificationsByRecipientId(UUID recipientId) {
        List<Notification> notifications = notificationService.getNotificationsByRecipientId(recipientId);
        return dtoMapper.mapNotificationListToNotificationResponseDtoList(notifications);
    }

    @PreAuthorize("hasAuthority('ROLE_USER_'.concat(@userService.getUserIdByEmail(#email)))")
    public List<NotificationResponseDto> getNotificationsByEmail(EmailAddress email) {
        UUID recipientId = userService.getUserIdByEmail(email);
        return this.getNotificationsByRecipientId(recipientId);
    }

    @PreAuthorize("hasAuthority('ROLE_USER_'.concat(@notificationService.getGetRecipientIdByNotificationId(#id)))")
    public void patchNotificationReadState(UUID id, NotificationPatchIsReadDto notificationPatchIsReadDto) {
        notificationService.patchReadState(id, notificationPatchIsReadDto.getIsRead());
    }

    @PreAuthorize("hasAuthority('ROLE_USER_'.concat(@notificationService.getGetRecipientIdByNotificationId(#id)))")
    public void deleteNotificationById(UUID id) {
        notificationService.deleteById(id);
    }


    // phase

    @PreAuthorize("hasAnyAuthority(" +
            "'ROLE_PROJECT_ADMIN_'.concat(@phaseDomainService.getProjectIdByPhaseId(#id)), " +
            "'ROLE_PROJECT_MEMBER_'.concat(@phaseDomainService.getProjectIdByPhaseId(#id)))")
    public PhaseResponseDto getPhaseById(UUID id) {
        Phase phase = phaseDomainService.getPhaseById(id);
        return dtoMapper.mapPhaseToPhaseResponseDto(phase);
    }

    @PreAuthorize("hasAnyAuthority(" +
            "'ROLE_PROJECT_ADMIN_'.concat(#projectId), " +
            "'ROLE_PROJECT_MEMBER_'.concat(#projectId))")
    public List<PhaseResponseDto> getPhasesByProjectId(UUID projectId) {
        List<Phase> phases = phaseDomainService.getPhasesByProjectId(projectId);
        return dtoMapper.mapPhaseListToPhaseResponseDtoList(phases);
    }

    @PreAuthorize("hasAuthority('ROLE_PROJECT_ADMIN_'.concat(#phasePostDto.projectId))")
    public PhaseResponseDto addPhase(PhasePostDto phasePostDto) {
        Phase phase = phaseDomainService.createPhase(
                dtoMapper.mapPhasePostDtoToPhase(phasePostDto), phasePostDto.getPreviousPhaseId()
        );
        return dtoMapper.mapPhaseToPhaseResponseDto(phase);
    }


    @PreAuthorize("hasAuthority('ROLE_PROJECT_ADMIN_'.concat(@phaseDomainService.getProjectIdByPhaseId(#id)))")
    public void patchPhaseName(UUID id, PhasePatchNameDto phasePatchNameDto) {
        phaseDomainService.patchPhaseName(id, phasePatchNameDto.getName());
    }

    @PreAuthorize("hasAuthority('ROLE_PROJECT_ADMIN_'.concat(@phaseDomainService.getProjectIdByPhaseId(#id)))")
    public void patchPhasePosition(UUID id, PhasePatchPositionDto phasePatchPositionDto) {
        phaseDomainService.patchPhasePosition(id, phasePatchPositionDto.getPreviousPhase());
    }

    @PreAuthorize("hasAuthority('ROLE_PROJECT_ADMIN_'.concat(@phaseDomainService.getProjectIdByPhaseId(#id)))")
    public void deletePhaseById(UUID id) {
        phaseDomainService.deleteById(id);
    }


    // project

    @PreAuthorize("hasAnyAuthority(" +
            "'ROLE_PROJECT_ADMIN_'.concat(#id), " +
            "'ROLE_PROJECT_MEMBER_'.concat(#id))")
    public ProjectResponseDto fetchProjectById(UUID id) {
        Project project = projectService.getProjectById(id);
        return dtoMapper.mapProjectToProjectResponseDto(project);
    }

    public ProjectResponseDto addProject(ProjectPostDto projectPostDto, UUID postingUserId) {
        Project project = projectService.addProject(
                dtoMapper.mapProjectPostDtoToProject(projectPostDto),
                postingUserId
        );
        return dtoMapper.mapProjectToProjectResponseDto(project);
    }

    public ProjectResponseDto addProject(ProjectPostDto projectPostDto, EmailAddress postingUserEmail) {
        UUID userId = userService.getUserIdByEmail(postingUserEmail);
        return this.addProject(projectPostDto, userId);
    }

    @PreAuthorize("hasAuthority('ROLE_PROJECT_ADMIN_'.concat(#id))")
    public void deleteProjectById(UUID id) {
        projectService.deleteProjectById(id);
    }

    @PreAuthorize("hasAuthority('ROLE_PROJECT_ADMIN_'.concat(#id))")
    public void patchProjectById(UUID id, ProjectPatchDto projectPatchDto) {
        projectService.patchProjectById(
                id,
                projectPatchDto.getName(),
                projectPatchDto.getDescription()
        );
    }


    // ticket

    @PreAuthorize("hasAnyAuthority(" +
            "'ROLE_PROJECT_ADMIN_'.concat(@ticketDomainService.getProjectIdByTicketId(#id)), " +
            "'ROLE_PROJECT_MEMBER_'.concat(@ticketDomainService.getProjectIdByTicketId(#id)))")
    public TicketResponseDto getTicketById(UUID id) {
        Ticket ticket = ticketDomainService.getTicketById(id);
        return dtoMapper.mapTicketToTicketResponseDto(ticket);
    }

    @PreAuthorize("hasAnyAuthority(" +
            "'ROLE_PROJECT_ADMIN_'.concat(@phaseDomainService.getProjectIdByPhaseId(#phaseId)), " +
            "'ROLE_PROJECT_MEMBER_'.concat(@phaseDomainService.getProjectIdByPhaseId(#phaseId)))")
    public List<TicketResponseDto> getTicketsByPhaseId(UUID phaseId) {
        List<Ticket> tickets = ticketDomainService.getTicketsByPhaseId(phaseId);
        return dtoMapper.mapTicketListToTicketResponseDtoList(tickets);
    }

    @PreAuthorize("hasAuthority('ROLE_USER_'.concat(#assigneeId))")
    public List<TicketResponseDto> getTicketsByAssigneeId(UUID assigneeId) {
        List<Ticket> tickets = ticketDomainService.getTicketsByAssigneeId(assigneeId);
        return dtoMapper.mapTicketListToTicketResponseDtoList(tickets);
    }

    @PreAuthorize("hasAnyAuthority(" +
            "'ROLE_PROJECT_ADMIN_'.concat(#projectId), " +
            "'ROLE_PROJECT_MEMBER_'.concat(#projectId))")
    public List<TicketResponseDto> getTicketsByProjectId(UUID projectId) {
        List<Ticket> tickets = ticketDomainService.getTicketsByProjectId(projectId);
        return dtoMapper.mapTicketListToTicketResponseDtoList(tickets);
    }

    @PreAuthorize("hasAnyAuthority(" +
            "'ROLE_PROJECT_ADMIN_'.concat(#ticketPostDto.projectId), " +
            "'ROLE_PROJECT_MEMBER_'.concat(#ticketPostDto.projectId))")
    public TicketResponseDto addTicket(TicketPostDto ticketPostDto, EmailAddress postingUserEmail) {
        Ticket ticket = ticketDomainService.addTicket(
                dtoMapper.mapTicketPostDtoToTicket(ticketPostDto, null),
                userService.getUserIdByEmail(postingUserEmail)
        );
        return dtoMapper.mapTicketToTicketResponseDto(ticket);
    }

    @PreAuthorize("hasAnyAuthority(" +
            "'ROLE_PROJECT_ADMIN_'.concat(@ticketDomainService.getProjectIdByTicketId(#id)), " +
            "'ROLE_PROJECT_MEMBER_'.concat(@ticketDomainService.getProjectIdByTicketId(#id)))")
    public void patchTicketById(UUID id, TicketPatchDto ticketPatchDto) {
        ticketDomainService.patchTicket(
                id,
                ticketPatchDto.getTitle(),
                ticketPatchDto.getDescription(),
                ticketPatchDto.getDueTime(),
                ticketPatchDto.getPhaseId(),
                ticketPatchDto.getAssigneeIds()
        );
    }

    @PreAuthorize("hasAnyAuthority(" +
            "'ROLE_PROJECT_ADMIN_'.concat(@ticketDomainService.getProjectIdByTicketId(#id)), " +
            "'ROLE_PROJECT_MEMBER_'.concat(@ticketDomainService.getProjectIdByTicketId(#id)))")
    public void deleteTicketById(UUID id) {
        ticketDomainService.deleteTicketById(id);
    }


    // user

    public UserResponseDto getUserById(UUID id) {
        User user = userService.getUserById(id);
        return dtoMapper.mapUserToUserResponseDto(user);
    }

    public UserResponseDto getByEMailAddress(EmailAddress eMailAddress) {
        User user = userService.getUserByEMailAddress(eMailAddress);
        return dtoMapper.mapUserToUserResponseDto(user);
    }

    public UserResponseDto addUser(UserPostDto userPostDto) {
        User user = userService.addUser(
                dtoMapper.mapUserPostDtoToUser(userPostDto)
        );

        return dtoMapper.mapUserToUserResponseDto(user);
    }

    @PreAuthorize("hasAuthority('ROLE_USER_'.concat(#id))")
    public void patchUserById(UUID id, UserPatchDto userPatchDto) {
        userService.patchUserById(
                id,
                userPatchDto.getName(),
                userPatchDto.getEmail()
        );
    }

    @PreAuthorize("hasAuthority('ROLE_USER_'.concat(#id))")
    public void deleteUserById(UUID id) {
        userService.deleteById(id);
    }
}
