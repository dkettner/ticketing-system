package com.kett.TicketSystem.project.application;

import com.kett.TicketSystem.common.events.*;
import com.kett.TicketSystem.common.exceptions.ImpossibleException;
import com.kett.TicketSystem.project.domain.Project;
import com.kett.TicketSystem.project.domain.exceptions.*;
import com.kett.TicketSystem.project.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, ApplicationEventPublisher eventPublisher) {
        this.projectRepository = projectRepository;
        this.eventPublisher = eventPublisher;
    }


    // create

    public Project addProject(Project project, UUID userId) {
        Project initializedProject = projectRepository.save(project);
        eventPublisher.publishEvent(new ProjectCreatedEvent(initializedProject.getId(), userId));
        return initializedProject;
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

    public void patchProjectById(UUID id, String newName, String newDescription) throws ProjectException, NoProjectFoundException {
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

    public void deleteProjectById(UUID id) throws NoProjectFoundException {
        Long numOfDeletedProjects = projectRepository.removeById(id);

        if (numOfDeletedProjects == 0) {
            throw new NoProjectFoundException("could not delete because there was no project with id: " + id);
        } else if (numOfDeletedProjects > 1) {
            throw new ImpossibleException(
                    "!!! This should not happen. " +
                    "Multiple projects were deleted when deleting project with id: " + id
            );
        } else {
            eventPublisher.publishEvent(new ProjectDeletedEvent(id));
        }
    }


    // event listeners

    @EventListener
    @Async
    public void handleUserCreated(UserCreatedEvent userCreatedEvent) {
        Project defaultProject = new Project(
                "Example Project",
                "This project was created automatically. Use it to get accustomed to everything."
        );
        Project initializedProject = projectRepository.save(defaultProject);
        eventPublisher.publishEvent(new DefaultProjectCreatedEvent(initializedProject.getId(), userCreatedEvent.getUserId()));
    }

    @EventListener
    @Async
    public void handleLastProjectMemberDeletedEvent(LastProjectMemberDeletedEvent lastProjectMemberDeletedEvent) {
        this.deleteProjectById(lastProjectMemberDeletedEvent.getProjectId());
    }
}
