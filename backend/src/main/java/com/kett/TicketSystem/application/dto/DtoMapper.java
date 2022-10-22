package com.kett.TicketSystem.application.dto;

import com.kett.TicketSystem.project.domain.Project;
import org.modelmapper.ModelMapper;

public class DtoMapper {
    private final ModelMapper modelMapper = new ModelMapper();

    public DtoMapper() {
        modelMapper.typeMap(Project.class, ProjectDto.class).addMappings(mapper -> {
            mapper.map(Project::getId, ProjectDto::setId);
            mapper.map(Project::getName, ProjectDto::setName);
            mapper.map(Project::getDescription, ProjectDto::setDescription);
            mapper.map(Project::getCreatorId, ProjectDto::setCreatorId);
            mapper.map(Project::getCreationTime, ProjectDto::setCreationTime);
            mapper.map(Project::getMemberIds, ProjectDto::setMemberIds);
        });
    }

    public ProjectDto mapProjectToProjectDto(Project project) {
        return modelMapper.map(project, ProjectDto.class);
    }
}
