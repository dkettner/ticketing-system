package com.kett.TicketSystem.application;

import com.kett.TicketSystem.application.dto.DtoMapper;
import com.kett.TicketSystem.application.dto.ProjectResponseDto;
import com.kett.TicketSystem.application.dto.TicketResponseDto;
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

    public ProjectResponseDto fetchProjectResponseDtoById(UUID id) {
        Project project = projectService.getProjectById(id);
        return dtoMapper.mapProjectToProjectResponseDto(project);
    }

    public List<TicketResponseDto> fetchTicketDtosByProjectId(UUID id) {
        List<Ticket> tickets = projectService.getTicketsByProjectId(id);
        return dtoMapper.mapTicketListToTicketResponseDtoList(tickets);
    }
}
