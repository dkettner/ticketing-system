package com.kett.TicketSystem.application;

import com.kett.TicketSystem.application.exceptions.ImpossibleException;
import com.kett.TicketSystem.authentication.AuthenticationService;
import com.kett.TicketSystem.authentication.dto.AuthenticationPostDto;
import com.kett.TicketSystem.domainprimitives.EmailAddress;
import com.kett.TicketSystem.membership.application.MembershipService;
import com.kett.TicketSystem.membership.application.dto.MembershipPostDto;
import com.kett.TicketSystem.membership.application.dto.MembershipResponseDto;
import com.kett.TicketSystem.membership.domain.Membership;
import com.kett.TicketSystem.membership.domain.Role;
import com.kett.TicketSystem.membership.domain.State;
import com.kett.TicketSystem.membership.domain.exceptions.InvalidProjectMembersException;
import com.kett.TicketSystem.phase.application.dto.PhasePostDto;
import com.kett.TicketSystem.phase.application.dto.PhaseResponseDto;
import com.kett.TicketSystem.phase.domain.Phase;
import com.kett.TicketSystem.project.application.ProjectService;
import com.kett.TicketSystem.project.application.dto.*;
import com.kett.TicketSystem.project.domain.Project;
import com.kett.TicketSystem.phase.application.PhaseService;
import com.kett.TicketSystem.project.domain.exceptions.NoProjectFoundException;
import com.kett.TicketSystem.project.domain.exceptions.PhaseIsNotEmptyException;
import com.kett.TicketSystem.ticket.application.TicketService;
import com.kett.TicketSystem.ticket.application.dto.TicketPostDto;
import com.kett.TicketSystem.ticket.application.dto.TicketResponseDto;
import com.kett.TicketSystem.ticket.domain.Ticket;
import com.kett.TicketSystem.user.application.UserService;
import com.kett.TicketSystem.user.application.dto.UserPostDto;
import com.kett.TicketSystem.user.application.dto.UserResponseDto;
import com.kett.TicketSystem.user.domain.User;
import com.kett.TicketSystem.user.domain.exceptions.NoUserFoundException;
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
    private final AuthenticationService authenticationService;
    private final DtoMapper dtoMapper;

    @Autowired
    public TicketSystemService (ProjectService projectService, PhaseService phaseService,
                                TicketService ticketService, UserService userService,
                                MembershipService membershipService, AuthenticationService authenticationService) {
        this.projectService = projectService;
        this.phaseService = phaseService;
        this.ticketService = ticketService;
        this.userService = userService;
        this.membershipService = membershipService;
        this.authenticationService = authenticationService;
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

    public List<MembershipResponseDto> getMembershipsByProjectEmail(EmailAddress email) {
        UUID userId = userService.getUserIdByEmail(email);
        return this.getMembershipsByUserId(userId);
    }

    public List<MembershipResponseDto> getMembershipsByProjectId(UUID projectId) {
        List<Membership> memberships = membershipService.getMembershipsByProjectId(projectId);
        return dtoMapper.mapMembershipListToMembershipResponseDtoList(memberships);
    }

    public MembershipResponseDto addMembership(MembershipPostDto membershipPostDto) {
        // TODO: add proper validation once authentication is possible
        UUID projectId = membershipPostDto.getProjectId();
        if (!projectService.isExistentById(projectId)) {
            throw new NoProjectFoundException("could not find project with id: " + projectId);
        }
        UUID userId = membershipPostDto.getUserId();
        if (!userService.isExistentById(userId)) {
            throw new NoUserFoundException("could not find user with id: " + userId);
        }

        Membership membership = membershipService.addMembership(
                dtoMapper.mapMembershipPostDtoToMembership(membershipPostDto)
        );
        return dtoMapper.mapMembershipToMembershipResponseDto(membership);
    }

    // TODO: too dirty, this bypasses some checks
    private void addDefaultMembershipForProject(Project project, UUID postingUserId) {
        Membership defaultMembership = new Membership(project.getId(), postingUserId, Role.ADMIN);
        defaultMembership.setState(State.ACCEPTED);
        this.membershipService.addMembership(defaultMembership);
    }

    public void deleteMembershipById(UUID id) {
        Membership membership = membershipService.getMembershipById(id);
        this.removeUserFromAllTicketsOfProject(membership.getUserId(), membership.getProjectId());

        membershipService.deleteMembershipById(id);
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

    public PhaseResponseDto addPhase(PhasePostDto phasePostDto) {
        UUID projectId = phasePostDto.getProjectId();
        if (!projectService.isExistentById(projectId)) {
            throw new NoProjectFoundException("could not find project with id: " + projectId);
        }

        UUID previousPhaseId = phasePostDto.getPreviousPhaseId();
        Phase previousPhase = null;
        if (previousPhaseId != null) {
            previousPhase = phaseService.getPhaseById(phasePostDto.getPreviousPhaseId());
        }

        Phase phase = phaseService.addPhase(
                dtoMapper.mapPhasePostDtoToPhase(phasePostDto, previousPhase)
        );
        return dtoMapper.mapPhaseToPhaseResponseDto(phase);
    }

    private void addDefaultPhasesForProject(Project project) {
        PhasePostDto toDo = new PhasePostDto(project.getId(), "TO DO", null);
        PhasePostDto doing = new PhasePostDto(project.getId(), "DOING", null);
        PhasePostDto review = new PhasePostDto(project.getId(), "REVIEW", null);
        PhasePostDto done = new PhasePostDto(project.getId(), "DONE", null);

        this.addPhase(done);
        this.addPhase(review);
        this.addPhase(doing);
        this.addPhase(toDo);
    }

    public void deletePhaseById(UUID id) {
        if (ticketService.hasTicketsWithPhaseId(id)) {
            throw new PhaseIsNotEmptyException("phase with id: " + id + " is not empty and can not be deleted");
        }
        phaseService.deleteById(id);
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

    public ProjectResponseDto addProject(ProjectPostDto projectPostDto, UUID postingUserId) {
        Project project = projectService.addProject(
                dtoMapper.mapProjectPostDtoToProject(projectPostDto)
        );

        this.addDefaultMembershipForProject(project, postingUserId);
        this.addDefaultPhasesForProject(project);

        return dtoMapper.mapProjectToProjectResponseDto(project);
    }

    public ProjectResponseDto addProject(ProjectPostDto projectPostDto, EmailAddress postingUserEmail) {
        UUID userId = userService.getUserIdByEmail(postingUserEmail);
        return this.addProject(projectPostDto, userId);
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

    private void addDefaultProjectForNewUser(UUID userId) {
        ProjectPostDto defaultProject = new ProjectPostDto(
                "Example Project",
                "This project was automatically created. Use it to get accustomed to everything."
        );

        this.addProject(defaultProject, userId);
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

    public TicketResponseDto addTicket(TicketPostDto ticketPostDto) {
        if (!projectService.isExistentById(ticketPostDto.getProjectId())) {
            throw new NoProjectFoundException("could not find project with id: " + ticketPostDto.getProjectId());
        }
        if (!membershipService.allUsersAreProjectMembers(ticketPostDto.getAssigneeIds(), ticketPostDto.getProjectId())) {
            throw new InvalidProjectMembersException(
                    "not all assignees are not part of the project with id: " + ticketPostDto.getProjectId()
            );
        }

        UUID phaseId = phaseService
                .getFirstPhaseByProjectId(ticketPostDto.getProjectId())
                .orElseThrow(() -> new ImpossibleException(
                        "!!! This should not happen. " +
                        "The project with id: " + ticketPostDto.getProjectId() + " exists but has no phases."
                ))
                .getId();

        Ticket ticket = ticketService.addTicket(
                dtoMapper.mapTicketPostDtoToTicket(ticketPostDto, phaseId)
        );
        return dtoMapper.mapTicketToTicketResponseDto(ticket);
    }

    private void removeUserFromAllTicketsOfProject(UUID userId, UUID projectId) {
        List<UUID> projectPhaseIds =
                phaseService
                        .getPhasesByProjectId(projectId)
                        .stream()
                        .map(Phase::getId)
                        .toList();

        List<Ticket> tickets = ticketService.getTicketsByPhaseIdsAndAssigneeId(projectPhaseIds, userId);
        tickets.forEach(ticket -> ticket.removeAssignee(userId));
        ticketService.saveAll(tickets);
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

        this.addDefaultProjectForNewUser(user.getId());

        return dtoMapper.mapUserToUserResponseDto(user);
    }


    // authentication

    public String authenticateUser(AuthenticationPostDto authenticationPostDto) {
        return authenticationService
                .authenticateUser(authenticationPostDto.getEmail(), authenticationPostDto.getPassword());
    }
}
