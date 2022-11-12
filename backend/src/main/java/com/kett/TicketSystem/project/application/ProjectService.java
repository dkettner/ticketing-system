package com.kett.TicketSystem.project.application;

import com.kett.TicketSystem.application.exceptions.ImpossibleException;
import com.kett.TicketSystem.project.domain.Project;
import com.kett.TicketSystem.project.domain.exceptions.*;
import com.kett.TicketSystem.project.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }


    // create
    public Project addProject(Project project) {
        return projectRepository.save(project);
    }


    // read

    public Project getProjectById(UUID id) throws NoProjectFoundException {
        return projectRepository
                .findById(id)
                .orElseThrow(() -> new NoProjectFoundException("could not find project with id: " + id));
    }

    public Boolean isExistentById(UUID id) {
        return projectRepository.existsById(id);
    }


    // update

    public void patchProjectById(UUID id, String newName, String newDescription) {
        Project existingProject = getProjectById(id);
        if (newName != null) {
            existingProject.setName(newName);
        }
        if (newDescription != null) {
            existingProject.setDescription(newDescription);
        }
        projectRepository.save(existingProject);
    }


    // delete

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
}
