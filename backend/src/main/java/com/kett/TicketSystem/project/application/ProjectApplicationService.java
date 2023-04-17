package com.kett.TicketSystem.project.application;

import com.kett.TicketSystem.application.DtoMapper;
import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import com.kett.TicketSystem.project.application.dto.ProjectPatchDto;
import com.kett.TicketSystem.project.application.dto.ProjectPostDto;
import com.kett.TicketSystem.project.application.dto.ProjectResponseDto;
import com.kett.TicketSystem.project.domain.Project;
import com.kett.TicketSystem.project.domain.ProjectDomainService;
import com.kett.TicketSystem.user.application.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProjectApplicationService {
    private final ProjectDomainService projectDomainService;
    private final UserService userService; // TODO: remove UserService dependency
    private final DtoMapper dtoMapper;

    @Autowired
    public ProjectApplicationService(
            ProjectDomainService projectDomainService,
            UserService userService, // TODO: remove UserService dependency
            DtoMapper dtoMapper
    ) {
        this.projectDomainService = projectDomainService;
        this.userService = userService;
        this.dtoMapper = dtoMapper;
    }

    @PreAuthorize("hasAnyAuthority(" +
            "'ROLE_PROJECT_ADMIN_'.concat(#id), " +
            "'ROLE_PROJECT_MEMBER_'.concat(#id))")
    public ProjectResponseDto fetchProjectById(UUID id) {
        Project project = projectDomainService.getProjectById(id);
        return dtoMapper.mapProjectToProjectResponseDto(project);
    }

    public ProjectResponseDto addProject(ProjectPostDto projectPostDto, UUID postingUserId) {
        Project project = projectDomainService.addProject(
                dtoMapper.mapProjectPostDtoToProject(projectPostDto),
                postingUserId
        );
        return dtoMapper.mapProjectToProjectResponseDto(project);
    }

    public ProjectResponseDto addProject(ProjectPostDto projectPostDto, EmailAddress postingUserEmail) {
        UUID userId = userService.getUserIdByEmail(postingUserEmail);
        return this.addProject(projectPostDto, userId);
    }

    @PreAuthorize("hasAuthority('ROLE_PROJECT_ADMIN_'.concat(#id))")
    public void deleteProjectById(UUID id) {
        projectDomainService.deleteProjectById(id);
    }

    @PreAuthorize("hasAuthority('ROLE_PROJECT_ADMIN_'.concat(#id))")
    public void patchProjectById(UUID id, ProjectPatchDto projectPatchDto) {
        projectDomainService.patchProjectById(
                id,
                projectPatchDto.getName(),
                projectPatchDto.getDescription()
        );
    }
}
