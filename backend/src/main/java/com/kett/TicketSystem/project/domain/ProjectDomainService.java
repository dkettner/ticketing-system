package com.kett.TicketSystem.project.domain;

import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import com.kett.TicketSystem.common.exceptions.ImpossibleException;
import com.kett.TicketSystem.common.exceptions.NoProjectFoundException;
import com.kett.TicketSystem.membership.domain.events.LastProjectMemberDeletedEvent;
import com.kett.TicketSystem.project.domain.consumedData.UserDataOfProject;
import com.kett.TicketSystem.project.domain.events.DefaultProjectCreatedEvent;
import com.kett.TicketSystem.project.domain.events.ProjectCreatedEvent;
import com.kett.TicketSystem.project.domain.events.ProjectDeletedEvent;
import com.kett.TicketSystem.project.domain.exceptions.*;
import com.kett.TicketSystem.project.repository.ProjectRepository;
import com.kett.TicketSystem.project.repository.UserDataOfProjectRepository;
import com.kett.TicketSystem.user.domain.events.UserCreatedEvent;
import com.kett.TicketSystem.user.domain.events.UserDeletedEvent;
import com.kett.TicketSystem.user.domain.events.UserPatchedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProjectDomainService {
    private final ProjectRepository projectRepository;
    private final UserDataOfProjectRepository userDataOfProjectRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public ProjectDomainService(ProjectRepository projectRepository, UserDataOfProjectRepository userDataOfProjectRepository, ApplicationEventPublisher eventPublisher) {
        this.projectRepository = projectRepository;
        this.userDataOfProjectRepository = userDataOfProjectRepository;
        this.eventPublisher = eventPublisher;
    }

    // create

    public Project addProject(Project project, EmailAddress emailAddress) {
        UUID userId = getUserIdByUserEmailAddress(emailAddress);
        Project initializedProject = projectRepository.save(project);
        eventPublisher.publishEvent(new ProjectCreatedEvent(initializedProject.getId(), userId));
        return initializedProject;
    }

    private UUID getUserIdByUserEmailAddress(EmailAddress emailAddress) {
        List<UserDataOfProject> userData = userDataOfProjectRepository.findByUserEmailEquals(emailAddress);
        if (userData.isEmpty()) {
            throw new ImpossibleException("no user data found for user: " + emailAddress.toString());
        }
        return userData.get(0).getUserId();
    }


    // read

    public Project getProjectById(UUID id) throws NoProjectFoundException {
        return projectRepository
                .findById(id)
                .orElseThrow(() -> new NoProjectFoundException("could not find project with id: " + id));
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
        userDataOfProjectRepository.save(new UserDataOfProject(userCreatedEvent.getUserId(), userCreatedEvent.getEmailAddress()));
        Project defaultProject = new Project(
                "Example Project",
                "This project was created automatically. Use it to get accustomed to everything."
        );
        Project initializedProject = projectRepository.save(defaultProject);
        eventPublisher.publishEvent(new DefaultProjectCreatedEvent(initializedProject.getId(), userCreatedEvent.getUserId()));
    }

    @EventListener
    public void handleLastProjectMemberDeletedEvent(LastProjectMemberDeletedEvent lastProjectMemberDeletedEvent) {
        this.deleteProjectById(lastProjectMemberDeletedEvent.getProjectId());
    }

    @EventListener
    @Async
    public void handleUserPatchedEvent(UserPatchedEvent userPatchedEvent) {
        UserDataOfProject userDataOfProject =
                userDataOfProjectRepository
                        .findByUserId(userPatchedEvent.getUserId())
                        .get(0);
        userDataOfProject.setUserEmail(userPatchedEvent.getEmailAddress());
        userDataOfProjectRepository.save(userDataOfProject);
    }

    @EventListener
    @Async
    public void handleUserDeletedEvent(UserDeletedEvent userDeletedEvent) {
        userDataOfProjectRepository.deleteByUserId(userDeletedEvent.getUserId());
    }
}
