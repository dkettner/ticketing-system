package com.kett.TicketSystem.project.domain;

import org.modelmapper.ModelMapper;

public class ProjectPatcher {
    private final ModelMapper modelMapper = new ModelMapper();

    public ProjectPatcher() {
        modelMapper.getConfiguration().isSkipNullEnabled();

        modelMapper.typeMap(Project.class, Project.class).addMappings(mapper -> {
            mapper.map(Project::getName, Project::setName);
            mapper.map(Project::getDescription, Project::setDescription);
        });
    }
}
