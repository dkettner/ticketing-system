package com.kett.TicketSystem.application;

import com.kett.TicketSystem.membership.application.dto.MembershipResponseDto;
import com.kett.TicketSystem.membership.domain.Membership;
import com.kett.TicketSystem.phase.application.dto.PhaseResponseDto;
import com.kett.TicketSystem.phase.domain.Phase;
import com.kett.TicketSystem.ticket.application.dto.TicketPostDto;
import com.kett.TicketSystem.ticket.application.dto.TicketResponseDto;
import com.kett.TicketSystem.project.application.dto.ProjectPostDto;
import com.kett.TicketSystem.project.application.dto.ProjectResponseDto;
import com.kett.TicketSystem.project.domain.Project;
import com.kett.TicketSystem.ticket.domain.Ticket;
import com.kett.TicketSystem.user.application.dto.UserResponseDto;
import com.kett.TicketSystem.user.domain.User;
import org.modelmapper.ModelMapper;

import java.util.List;

public class DtoMapper {
    private final ModelMapper modelMapper = new ModelMapper();

    public DtoMapper() {
        modelMapper.typeMap(Ticket.class, TicketResponseDto.class).addMappings(mapper -> {
            mapper.map(Ticket::getId, TicketResponseDto::setId);
            mapper.map(Ticket::getTitle, TicketResponseDto::setTitle);
            mapper.map(Ticket::getDescription, TicketResponseDto::setDescription);
            mapper.map(Ticket::getCreationTime, TicketResponseDto::setCreationTime);
            mapper.map(Ticket::getDueTime, TicketResponseDto::setDueTime);
            mapper.map(Ticket::getPhaseId, TicketResponseDto:: setPhaseId);
            mapper.map(Ticket::getAssigneeIds, TicketResponseDto::setAssigneeIds);
        });
        modelMapper.typeMap(Project.class, ProjectResponseDto.class).addMappings(mapper -> {
            mapper.map(Project::getId, ProjectResponseDto::setId);
            mapper.map(Project::getName, ProjectResponseDto::setName);
            mapper.map(Project::getDescription, ProjectResponseDto::setDescription);
            mapper.map(Project::getCreationTime, ProjectResponseDto::setCreationTime);
        });
        modelMapper.typeMap(Phase.class, PhaseResponseDto.class).addMappings(mapper -> {
            mapper.map(Phase::getId, PhaseResponseDto::setId);
            mapper.map(Phase::getProjectId, PhaseResponseDto::setProjectId);
            mapper.map(Phase::getName, PhaseResponseDto::setName);
            mapper.map(Phase::getPreviousPhase, PhaseResponseDto::setPreviousPhase);
            mapper.map(Phase::getNextPhase, PhaseResponseDto::setNextPhase);
        });
        modelMapper.typeMap(Membership.class, MembershipResponseDto.class).addMappings(mapper -> {
            mapper.map(Membership::getId, MembershipResponseDto::setId);
            mapper.map(Membership::getProjectId, MembershipResponseDto::setProjectId);
            mapper.map(Membership::getUserId, MembershipResponseDto::setUserId);
            mapper.map(Membership::getRole, MembershipResponseDto::setRole);
            mapper.map(Membership::getState, MembershipResponseDto::setState);
        });
        modelMapper.typeMap(User.class, UserResponseDto.class).addMappings(mapper -> {
            mapper.map(User::getId, UserResponseDto::setId);
            mapper.map(User::getName, UserResponseDto::setName);
            mapper.map(user -> user.getEMailAddress().toString(), UserResponseDto::setEMailAddress);
        });
    }

    public MembershipResponseDto mapMembershipToMembershipResponseDto(Membership membership) {
        return modelMapper.map(membership, MembershipResponseDto.class);
    }

    public TicketResponseDto mapTicketToTicketResponseDto(Ticket ticket) {
        return modelMapper.map(ticket, TicketResponseDto.class);
    }

    public List<TicketResponseDto> mapTicketListToTicketResponseDtoList(List<Ticket> tickets) {
        return tickets
                .stream()
                .map(ticket -> modelMapper.map(ticket, TicketResponseDto.class))
                .toList();
    }

    public ProjectResponseDto mapProjectToProjectResponseDto(Project project) {
        return modelMapper.map(project, ProjectResponseDto.class);
    }

    public Project mapProjectPostDtoToProject(ProjectPostDto projectPostDto) {
        return new Project(projectPostDto.getName(), projectPostDto.getDescription());
    }

    public Ticket mapTicketPostDtoToTicket(TicketPostDto ticketPostDto) {
        return new Ticket(
                ticketPostDto.getTitle(),
                ticketPostDto.getDescription(),
                ticketPostDto.getDueTime(),
                ticketPostDto.getAssigneeIds()
        );
    }

    public UserResponseDto mapUserToUserResponseDto(User user) {
        return modelMapper.map(user, UserResponseDto.class);
    }

    public PhaseResponseDto mapPhaseToPhaseResponseDto(Phase phase) {
        return modelMapper.map(phase, PhaseResponseDto.class);
    }

    public List<MembershipResponseDto> mapMembershipListToMembershipResponseDtoList(List<Membership> memberships) {
        return memberships
                .stream()
                .map(membership -> modelMapper.map(membership, MembershipResponseDto.class))
                .toList();
    }

    public List<PhaseResponseDto> mapPhaseListToPhaseResponseDtoList(List<Phase> phases) {
        return phases
                .stream()
                .map(phase -> modelMapper.map(phase, PhaseResponseDto.class))
                .toList();
    }
}
