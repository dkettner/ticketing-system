package com.kett.TicketSystem.application.dto;

import com.kett.TicketSystem.project.domain.Project;
import org.modelmapper.ModelMapper;

public class DtoMapper {
    private final ModelMapper modelMapper = new ModelMapper();

    public DtoMapper() {
        modelMapper.typeMap(Project.class, ProjectResponseDto.class).addMappings(mapper -> {
            mapper.map(Project::getId, ProjectResponseDto::setId);
            mapper.map(Project::getName, ProjectResponseDto::setName);
            mapper.map(Project::getDescription, ProjectResponseDto::setDescription);
            mapper.map(Project::getCreatorId, ProjectResponseDto::setCreatorId);
            mapper.map(Project::getCreationTime, ProjectResponseDto::setCreationTime);
            mapper.map(Project::getMemberIds, ProjectResponseDto::setMemberIds);
        });
    }

    public ProjectResponseDto mapProjectToProjectResponseDto(Project project) {
        return modelMapper.map(project, ProjectResponseDto.class);
    }
}
