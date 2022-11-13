package com.kett.TicketSystem.application;

import com.kett.TicketSystem.application.exceptions.ImpossibleException;
import com.kett.TicketSystem.authentication.AuthenticationService;
import com.kett.TicketSystem.authentication.dto.AuthenticationPostDto;
import com.kett.TicketSystem.domainprimitives.EmailAddress;
import com.kett.TicketSystem.membership.application.MembershipService;
import com.kett.TicketSystem.membership.application.dto.MembershipPatchRoleDto;
import com.kett.TicketSystem.membership.application.dto.MembershipPatchStateDto;
import com.kett.TicketSystem.membership.application.dto.MembershipPostDto;
import com.kett.TicketSystem.membership.application.dto.MembershipResponseDto;
import com.kett.TicketSystem.membership.domain.Membership;
import com.kett.TicketSystem.membership.domain.Role;
import com.kett.TicketSystem.membership.domain.State;
import com.kett.TicketSystem.membership.domain.exceptions.InvalidProjectMembersException;
import com.kett.TicketSystem.phase.application.dto.PhasePatchNameDto;
import com.kett.TicketSystem.phase.application.dto.PhasePatchPositionDto;
import com.kett.TicketSystem.phase.application.dto.PhasePostDto;
import com.kett.TicketSystem.phase.application.dto.PhaseResponseDto;
import com.kett.TicketSystem.phase.domain.Phase;
import com.kett.TicketSystem.phase.domain.exceptions.UnrelatedPhaseException;
import com.kett.TicketSystem.project.application.ProjectService;
import com.kett.TicketSystem.project.application.dto.*;
import com.kett.TicketSystem.project.domain.Project;
import com.kett.TicketSystem.phase.application.PhaseService;
import com.kett.TicketSystem.project.domain.exceptions.NoProjectFoundException;
import com.kett.TicketSystem.project.domain.exceptions.PhaseIsNotEmptyException;
import com.kett.TicketSystem.ticket.application.TicketService;
import com.kett.TicketSystem.ticket.application.dto.TicketPatchDto;
import com.kett.TicketSystem.ticket.application.dto.TicketPostDto;
import com.kett.TicketSystem.ticket.application.dto.TicketResponseDto;
import com.kett.TicketSystem.ticket.domain.Ticket;
import com.kett.TicketSystem.user.application.UserService;
import com.kett.TicketSystem.user.application.dto.UserPatchDto;
import com.kett.TicketSystem.user.application.dto.UserPostDto;
import com.kett.TicketSystem.user.application.dto.UserResponseDto;
import com.kett.TicketSystem.user.domain.User;
import com.kett.TicketSystem.user.domain.exceptions.NoUserFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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


    // authentication

    public String authenticateUser(AuthenticationPostDto authenticationPostDto) {
        return authenticationService
                .authenticateUser(authenticationPostDto.getEmail(), authenticationPostDto.getPassword());
    }


    // membership

    @PreAuthorize("hasAnyAuthority(" +
            "'ROLE_PROJECT_ADMIN_'.concat(@membershipService.getProjectIdByMembershipId(#id))," +
            "'ROLE_USER_'.concat(@membershipService.getUserIdByMembershipId(#id)))")
    public MembershipResponseDto getMemberShipById(UUID id) {
        Membership membership = membershipService.getMembershipById(id);
        return dtoMapper.mapMembershipToMembershipResponseDto(membership);
    }

    @PreAuthorize("hasAuthority('ROLE_USER_'.concat(#userId))")
    public List<MembershipResponseDto> getMembershipsByUserId(UUID userId) {
        List<Membership> memberships = membershipService.getMembershipsByUserId(userId);
        return dtoMapper.mapMembershipListToMembershipResponseDtoList(memberships);
    }

    @PreAuthorize("hasAuthority('ROLE_USER_'.concat(@userService.getUserIdByEmail(#email)))")
    public List<MembershipResponseDto> getMembershipsByEmail(EmailAddress email) {
        UUID userId = userService.getUserIdByEmail(email);
        return this.getMembershipsByUserId(userId);
    }

    @PreAuthorize("hasAnyAuthority(" +
            "'ROLE_PROJECT_ADMIN_'.concat(#projectId), " +
            "'ROLE_PROJECT_MEMBER_'.concat(#projectId))")
    public List<MembershipResponseDto> getMembershipsByProjectId(UUID projectId) {
        List<Membership> memberships = membershipService.getMembershipsByProjectId(projectId);
        return dtoMapper.mapMembershipListToMembershipResponseDtoList(memberships);
    }

    @PreAuthorize("hasAuthority('ROLE_PROJECT_ADMIN_'.concat(#membershipPostDto.projectId))")
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

    @PreAuthorize("hasAuthority('ROLE_USER'.concat(@membershipService.getUserIdByMembershipId(#id)))")
    public void patchMembershipState(UUID id, MembershipPatchStateDto membershipPatchStateDto) {
        membershipService.patchMemberShipState(id, membershipPatchStateDto.getState());
    }

    @PreAuthorize("hasAuthority('ROLE_PROJECT_ADMIN_'.concat(@membershipService.getProjectIdByMembershipId(#id)))")
    public void patchMembershipRole(UUID id, MembershipPatchRoleDto membershipPatchRoleDto) {
        membershipService.patchMembershipRole(id, membershipPatchRoleDto.getRole());
    }

    @PreAuthorize("hasAnyAuthority(" +
            "'ROLE_PROJECT_ADMIN_'.concat(@membershipService.getProjectIdByMembershipId(#id))," +
            "'ROLE_USER'.concat(@membershipService.getUserIdByMembershipId(#id)))")
    public void deleteMembershipById(UUID id) {
        Membership membership = membershipService.getMembershipById(id);
        this.removeUserFromAllTicketsOfProject(membership.getUserId(), membership.getProjectId());

        membershipService.deleteMembershipById(id);
    }

    // TODO: too dirty, this bypasses some checks
    private void addDefaultMembershipForProject(Project project, UUID postingUserId) {
        Membership defaultMembership = new Membership(project.getId(), postingUserId, Role.ADMIN);
        defaultMembership.setState(State.ACCEPTED);
        this.membershipService.addMembership(defaultMembership);
    }


    // phase

    @PreAuthorize("hasAnyAuthority(" +
            "'ROLE_PROJECT_ADMIN_'.concat(@phaseService.getProjectIdByPhaseId(#id)), " +
            "'ROLE_PROJECT_MEMBER_'.concat(@phaseService.getProjectIdByPhaseId(#id)))")
    public PhaseResponseDto getPhaseById(UUID id) {
        Phase phase = phaseService.getPhaseById(id);
        return dtoMapper.mapPhaseToPhaseResponseDto(phase);
    }

    @PreAuthorize("hasAnyAuthority(" +
            "'ROLE_PROJECT_ADMIN_'.concat(#projectId), " +
            "'ROLE_PROJECT_MEMBER_'.concat(#projectId))")
    public List<PhaseResponseDto> getPhasesByProjectId(UUID projectId) {
        List<Phase> phases = phaseService.getPhasesByProjectId(projectId);
        return dtoMapper.mapPhaseListToPhaseResponseDtoList(phases);
    }

    @PreAuthorize("hasAuthority('ROLE_PROJECT_ADMIN_'.concat(#phasePostDto.projectId))")
    public PhaseResponseDto addPhaseAuthorized(PhasePostDto phasePostDto) {
        return addPhase(phasePostDto);
    }

    private PhaseResponseDto addPhase(PhasePostDto phasePostDto) {
        UUID projectId = phasePostDto.getProjectId();
        if (!projectService.isExistentById(projectId)) {
            throw new NoProjectFoundException("could not find project with id: " + projectId);
        }

        Phase phase = phaseService.addPhase(
                dtoMapper.mapPhasePostDtoToPhase(phasePostDto), phasePostDto.getPreviousPhaseId()
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


    @PreAuthorize("hasAuthority('ROLE_PROJECT_ADMIN_'.concat(@phaseService.getProjectIdByPhaseId(#id)))")
    public void patchPhaseName(UUID id, PhasePatchNameDto phasePatchNameDto) {
        phaseService.patchPhaseName(id, phasePatchNameDto.getName());
    }

    @PreAuthorize("hasAuthority('ROLE_PROJECT_ADMIN_'.concat(@phaseService.getProjectIdByPhaseId(#id)))")
    public void patchPhasePosition(UUID id, PhasePatchPositionDto phasePatchPositionDto) {
        phaseService.patchPhasePosition(id, phasePatchPositionDto.getPreviousPhase());
    }

    @PreAuthorize("hasAuthority('ROLE_PROJECT_ADMIN_'.concat(@phaseService.getProjectIdByPhaseId(#id)))")
    public void deletePhaseById(UUID id) {
        if (ticketService.hasTicketsWithPhaseId(id)) {
            throw new PhaseIsNotEmptyException("phase with id: " + id + " is not empty and can not be deleted");
        }
        phaseService.deleteById(id);
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

    @PreAuthorize("hasAuthority('ROLE_PROJECT_ADMIN_'.concat(#id))")
    public void deleteProjectById(UUID id) {
        projectService.deleteProjectById(id);
        phaseService.deletePhasesByProjectId(id);
        ticketService.deleteTicketsByProjectId(id);
        membershipService.deleteMembershipsByProjectId(id);
    }

    @PreAuthorize("hasAuthority('ROLE_PROJECT_ADMIN_'.concat(#id))")
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

    @PreAuthorize("hasAnyAuthority(" +
            "'ROLE_PROJECT_ADMIN_'.concat(@ticketService.getProjectIdByTicketId(#id)), " +
            "'ROLE_PROJECT_MEMBER_'.concat(@ticketService.getProjectIdByTicketId(#id)))")
    public TicketResponseDto getTicketById(UUID id) {
        Ticket ticket = ticketService.getTicketById(id);
        return dtoMapper.mapTicketToTicketResponseDto(ticket);
    }

    @PreAuthorize("hasAnyAuthority(" +
            "'ROLE_PROJECT_ADMIN_'.concat(@phaseService.getProjectIdByPhaseId(#phaseId)), " +
            "'ROLE_PROJECT_MEMBER_'.concat(@phaseService.getProjectIdByPhaseId(#phaseId)))")
    public List<TicketResponseDto> getTicketsByPhaseId(UUID phaseId) {
        List<Ticket> tickets = ticketService.getTicketsByPhaseId(phaseId);
        return dtoMapper.mapTicketListToTicketResponseDtoList(tickets);
    }

    @PreAuthorize("hasAuthority('ROLE_USER_'.concat(#assigneeId))")
    public List<TicketResponseDto> getTicketsByAssigneeId(UUID assigneeId) {
        List<Ticket> tickets = ticketService.getTicketsByAssigneeId(assigneeId);
        return dtoMapper.mapTicketListToTicketResponseDtoList(tickets);
    }

    @PreAuthorize("hasAnyAuthority(" +
            "'ROLE_PROJECT_ADMIN_'.concat(#projectId), " +
            "'ROLE_PROJECT_MEMBER_'.concat(#projectId))")
    public List<TicketResponseDto> getTicketsByProjectId(UUID projectId) {
        List<Ticket> tickets = ticketService.getTicketsByProjectId(projectId);
        return dtoMapper.mapTicketListToTicketResponseDtoList(tickets);
    }

    @PreAuthorize("hasAnyAuthority(" +
            "'ROLE_PROJECT_ADMIN_'.concat(#ticketPostDto.projectId), " +
            "'ROLE_PROJECT_MEMBER_'.concat(#ticketPostDto.projectId))")
    public TicketResponseDto addTicket(TicketPostDto ticketPostDto) {
        if (!projectService.isExistentById(ticketPostDto.getProjectId())) {
            throw new NoProjectFoundException("could not find project with id: " + ticketPostDto.getProjectId());
        }
        if (!membershipService.areAllUsersProjectMembers(ticketPostDto.getAssigneeIds(), ticketPostDto.getProjectId())) {
            throw new InvalidProjectMembersException(
                    "not all assignees are part of the project with id: " + ticketPostDto.getProjectId()
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

    @PreAuthorize("hasAnyAuthority(" +
            "'ROLE_PROJECT_ADMIN_'.concat(@ticketService.getProjectIdByTicketId(#id)), " +
            "'ROLE_PROJECT_MEMBER_'.concat(@ticketService.getProjectIdByTicketId(#id)))")
    public void patchTicketById(UUID id, TicketPatchDto ticketPatchDto) {
        UUID projectIdOfTicket = ticketService.getProjectIdByTicketId(id);
        if (ticketPatchDto.getPhaseId() != null) {
            UUID projectIdOfNewPhase = phaseService.getProjectIdByPhaseId(ticketPatchDto.getPhaseId());
            if (!projectIdOfTicket.equals(projectIdOfNewPhase)) {
                throw new UnrelatedPhaseException(
                        "The ticket with id: " + id +
                        "belongs to the project with id: " + projectIdOfTicket + ". " +
                        "But the new phase with id: " + ticketPatchDto.getPhaseId() +
                        "does not.");
            }
        }

        if (ticketPatchDto.getAssigneeIds() != null) {
            if (!membershipService.areAllUsersProjectMembers(ticketPatchDto.getAssigneeIds(), projectIdOfTicket)) {
                throw new InvalidProjectMembersException(
                        "not all assignees are part of the project with id: " + projectIdOfTicket
                );
            }
        }

        ticketService.patchTicket(
                id,
                ticketPatchDto.getTitle(),
                ticketPatchDto.getDescription(),
                ticketPatchDto.getDueTime(),
                ticketPatchDto.getPhaseId(),
                ticketPatchDto.getAssigneeIds()
        );
    }

    @PreAuthorize("hasAnyAuthority(" +
            "'ROLE_PROJECT_ADMIN_'.concat(@ticketService.getProjectIdByTicketId(#id)), " +
            "'ROLE_PROJECT_MEMBER_'.concat(@ticketService.getProjectIdByTicketId(#id)))")
    public void deleteTicketById(UUID id) {
        ticketService.deleteTicketById(id);
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

    @PreAuthorize("hasAuthority('ROLE_USER_'.concat(#id))")
    public void patchUserById(UUID id, UserPatchDto userPatchDto) {
        userService.patchUserById(
                id,
                userPatchDto.getName(),
                EmailAddress.fromString(userPatchDto.getEmail())
        );
    }
}
