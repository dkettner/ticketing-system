package com.kett.TicketSystem.project.application;

import com.kett.TicketSystem.project.domain.Project;
import com.kett.TicketSystem.project.domain.exceptions.*;
import com.kett.TicketSystem.project.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project getProjectById(UUID id) throws NoProjectFoundException {
        return projectRepository
                .findById(id)
                .orElseThrow(() -> new NoProjectFoundException("could not find project with id: " + id));
    }

    public Project addProject(Project project) {
        return projectRepository.save(project);
    }

    public void deleteProjectById(UUID id) {
        Long numOfDeletedProjects = projectRepository.removeById(id);

        if (numOfDeletedProjects == 0) {
            throw new NoProjectFoundException("could not delete because there was no project with id: " + id);
        } else if (numOfDeletedProjects > 1) {
            throw new ImpossibleException(
                    "!!! This should not happen. " +
                    "Multiple projects were deleted when deleting project with id: " + id
            );
        }
    }

    // TODO: clean this up
    public void patchProjectById(UUID id, String newName, String newDescription, List<UUID> newOwnerIds, List<UUID> newMemberIds) {
        if (newMemberIds != null && newMemberIds.isEmpty()) {
            throw new ProjectException(
                    "Cannot patch memberIds with empty list. " +
                    "If you do not want to patch memberIds, use null instead of empty list"
            );
        }

        Project existingProject = getProjectById(id);
        if (newName != null) {
            existingProject.setName(newName);
        }
        if (newDescription != null) {
            existingProject.setDescription(newDescription);
        }
        if (newOwnerIds != null) {
            existingProject.setOwnerIds(newOwnerIds);
            // TODO: check integrity of memberIds
        }
        if (newMemberIds != null) {
            existingProject.setMemberIds(newMemberIds);
        }
        projectRepository.save(existingProject);
    }

    // TODO: only for testing
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }
}
