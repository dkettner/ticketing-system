package com.kett.TicketSystem.application.dto;

import com.kett.TicketSystem.project.domain.Project;
import com.kett.TicketSystem.project.domain.Ticket;
import org.modelmapper.ModelMapper;

import java.util.List;

public class DtoMapper {
    private final ModelMapper modelMapper = new ModelMapper();

    public DtoMapper() {
        modelMapper.typeMap(Ticket.class, TicketResponseDto.class).addMappings(mapper -> {
            mapper.map(Ticket::getTicketNumber, TicketResponseDto::setTicketNumber);
            mapper.map(Ticket::getTitle, TicketResponseDto::setTitle);
            mapper.map(Ticket::getDescription, TicketResponseDto::setDescription);
            mapper.map(Ticket::getCreationTime, TicketResponseDto::setCreationTime);
            mapper.map(Ticket::getDueTime, TicketResponseDto::setCreationTime);
            mapper.map(Ticket::getTicketStatus, TicketResponseDto:: setTicketStatus);
            mapper.map(Ticket::getCreatorId, TicketResponseDto::setCreatorId);
            mapper.map(Ticket::getAssigneeIds, TicketResponseDto::setAssigneeIds);
        });
        modelMapper.typeMap(Project.class, ProjectResponseDto.class).addMappings(mapper -> {
            mapper.map(Project::getId, ProjectResponseDto::setId);
            mapper.map(Project::getName, ProjectResponseDto::setName);
            mapper.map(Project::getDescription, ProjectResponseDto::setDescription);
            mapper.map(Project::getCreatorId, ProjectResponseDto::setCreatorId);
            mapper.map(Project::getCreationTime, ProjectResponseDto::setCreationTime);
            mapper.map(Project::getMemberIds, ProjectResponseDto::setMemberIds);
            // TODO: add proper mapping for List<Ticket> -> List<TicketResponseDto>
        });
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
        ProjectResponseDto projectResponseDto = modelMapper.map(project, ProjectResponseDto.class);
        projectResponseDto.setTickets(
                mapTicketListToTicketResponseDtoList(project.getTickets())
        );
        return projectResponseDto;
    }

    public Project mapProjectPostDtoToProject(ProjectPostDto projectPostDto) {
        return new Project(
                projectPostDto.getName(),
                projectPostDto.getDescription(),
                projectPostDto.getCreatorId(),
                projectPostDto.getMemberIds()
        );
    }
}
