package com.kett.TicketSystem.application;

import com.kett.TicketSystem.domainprimitives.EMailAddress;
import com.kett.TicketSystem.membership.application.MembershipService;
import com.kett.TicketSystem.membership.application.dto.MembershipResponseDto;
import com.kett.TicketSystem.membership.domain.Membership;
import com.kett.TicketSystem.phase.application.dto.PhaseResponseDto;
import com.kett.TicketSystem.phase.domain.Phase;
import com.kett.TicketSystem.project.application.ProjectService;
import com.kett.TicketSystem.project.application.dto.*;
import com.kett.TicketSystem.project.domain.Project;
import com.kett.TicketSystem.phase.application.PhaseService;
import com.kett.TicketSystem.ticket.application.TicketService;
import com.kett.TicketSystem.ticket.application.dto.TicketResponseDto;
import com.kett.TicketSystem.ticket.domain.Ticket;
import com.kett.TicketSystem.user.application.UserService;
import com.kett.TicketSystem.user.application.dto.UserResponseDto;
import com.kett.TicketSystem.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TicketSystemService {
    private final ProjectService projectService;
    private final PhaseService phaseService;
    private final TicketService ticketService;
    private final UserService userService;
    private final MembershipService membershipService;
    private final DtoMapper dtoMapper;

    @Autowired
    public TicketSystemService (ProjectService projectService, PhaseService phaseService,
                                TicketService ticketService, UserService userService,
                                MembershipService membershipService) {
        this.projectService = projectService;
        this.phaseService = phaseService;
        this.ticketService = ticketService;
        this.userService = userService;
        this.membershipService = membershipService;
        this.dtoMapper = new DtoMapper();
    }


    // membership

    public MembershipResponseDto getMemberShipById(UUID id) {
        Membership membership = membershipService.getMembershipById(id);
        return dtoMapper.mapMembershipToMembershipResponseDto(membership);
    }

    public List<MembershipResponseDto> getMembershipsByUserId(UUID userId) {
        List<Membership> memberships = membershipService.getMembershipsByUserId(userId);
        return dtoMapper.mapMembershipListToMembershipResponseDtoList(memberships);
    }

    public List<MembershipResponseDto> getMembershipsByProjectId(UUID projectId) {
        List<Membership> memberships = membershipService.getMembershipsByProjectId(projectId);
        return dtoMapper.mapMembershipListToMembershipResponseDtoList(memberships);
    }


    // phase

    public PhaseResponseDto getPhaseById(UUID id) {
        Phase phase = phaseService.getPhaseById(id);
        return dtoMapper.mapPhaseToPhaseResponseDto(phase);
    }

    public List<PhaseResponseDto> getPhasesByProjectId(UUID projectId) {
        List<Phase> phases = phaseService.getPhasesByProjectId(projectId);
        return dtoMapper.mapPhaseListToPhaseResponseDtoList(phases);
    }


    // project

    public ProjectResponseDto fetchProjectById(UUID id) {
        Project project = projectService.getProjectById(id);
        return dtoMapper.mapProjectToProjectResponseDto(project);
    }

    public List<ProjectResponseDto> fetchAllProjects() {
        List<Project> allProjects = projectService.getAllProjects();
        return allProjects
                .stream()
                .map(dtoMapper::mapProjectToProjectResponseDto)
                .toList();
    }

    public ProjectResponseDto addProject(ProjectPostDto projectPostDto) {
        Project project = projectService.addProject(
                dtoMapper.mapProjectPostDtoToProject(projectPostDto)
        );
        return dtoMapper.mapProjectToProjectResponseDto(project);
    }

    public void deleteProjectById(UUID id) {
        projectService.deleteProjectById(id);
    }

    // TODO: clean this up
    public void patchProjectById(UUID id, ProjectPatchDto projectPatchDto) {
        projectService.patchProjectById(
                id,
                projectPatchDto.getName(),
                projectPatchDto.getDescription()
        );
    }


    // ticket

    public TicketResponseDto getTicketById(UUID id) {
        Ticket ticket = ticketService.getTicketById(id);
        return dtoMapper.mapTicketToTicketResponseDto(ticket);
    }

    public List<TicketResponseDto> getTicketsByPhaseId(UUID phaseId) {
        List<Ticket> tickets = ticketService.getTicketsByPhaseId(phaseId);
        return dtoMapper.mapTicketListToTicketResponseDtoList(tickets);
    }

    public List<TicketResponseDto> getTicketsByAssigneeId(UUID assigneeId) {
        List<Ticket> tickets = ticketService.getTicketsByAssigneeId(assigneeId);
        return dtoMapper.mapTicketListToTicketResponseDtoList(tickets);
    }


    // user

    public UserResponseDto getUserById(UUID id) {
        User user = userService.getUserById(id);
        return dtoMapper.mapUserToUserResponseDto(user);
    }

    public UserResponseDto getByEMailAddress(EMailAddress eMailAddress) {
        User user = userService.getUserByEMailAddress(eMailAddress);
        return dtoMapper.mapUserToUserResponseDto(user);
    }
}
