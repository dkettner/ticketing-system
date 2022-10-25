package com.kett.TicketSystem.application;

import com.kett.TicketSystem.application.dto.*;
import com.kett.TicketSystem.project.application.ProjectService;
import com.kett.TicketSystem.project.domain.Project;
import com.kett.TicketSystem.project.domain.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TicketSystemService {
    private final ProjectService projectService;
    private final DtoMapper dtoMapper;

    @Autowired
    public TicketSystemService (ProjectService projectService) {
        this.projectService = projectService;
        this.dtoMapper = new DtoMapper();
    }

    public ProjectResponseDto fetchProjectById(UUID id) {
        Project project = projectService.getProjectById(id);
        return dtoMapper.mapProjectToProjectResponseDto(project);
    }

    public List<TicketResponseDto> fetchTicketsByProjectId(UUID id) {
        List<Ticket> tickets = projectService.getTicketsByProjectId(id);
        return dtoMapper.mapTicketListToTicketResponseDtoList(tickets);
    }

    public TicketResponseDto fetchTicketByProjectIdAndTicketNumber(UUID id, UUID ticketNumber) {
        Ticket ticket = projectService.getTicketByProjectIdAndTicketNumber(id, ticketNumber);
        return dtoMapper.mapTicketToTicketResponseDto(ticket);
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
                id, projectPatchDto.getName(),
                projectPatchDto.getDescription(),
                projectPatchDto.getMemberIds());
    }

    public TicketResponseDto addTicketToProject(UUID id, TicketPostDto ticketPostDto) {
        Ticket ticket = projectService.addTicketToProject(id, dtoMapper.mapTicketPostDtoToTicket(ticketPostDto));
        return dtoMapper.mapTicketToTicketResponseDto(ticket);
    }

    public void deleteTicketByProjectIdAndTicketNumber(UUID id, UUID ticketNumber) {
        projectService.deleteTicketByProjectIdAndTicketNumber(id, ticketNumber);
    }

    // TODO: clean this up
    public void patchTicket(UUID id, UUID ticketNumber, TicketPatchDto ticketPatchDto) {
        projectService.patchTicket(
                id,
                ticketNumber,
                ticketPatchDto.getTitle(),
                ticketPatchDto.getDescription(),
                ticketPatchDto.getDueTime(),
                ticketPatchDto.getTicketStatus(),
                ticketPatchDto.getAssigneeIds()
        );
    }
}
