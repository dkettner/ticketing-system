package com.kett.TicketSystem.project.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import com.kett.TicketSystem.common.exceptions.NoProjectFoundException;
import com.kett.TicketSystem.membership.domain.events.LastProjectMemberDeletedEvent;
import com.kett.TicketSystem.project.application.dto.ProjectPatchDto;
import com.kett.TicketSystem.project.application.dto.ProjectPostDto;
import com.kett.TicketSystem.project.domain.Project;
import com.kett.TicketSystem.project.domain.ProjectDomainService;
import com.kett.TicketSystem.project.domain.events.DefaultProjectCreatedEvent;
import com.kett.TicketSystem.project.domain.events.ProjectCreatedEvent;
import com.kett.TicketSystem.project.domain.events.ProjectDeletedEvent;
import com.kett.TicketSystem.project.repository.ProjectRepository;
import com.kett.TicketSystem.user.domain.events.UserCreatedEvent;
import com.kett.TicketSystem.user.repository.UserRepository;
import com.kett.TicketSystem.util.EventCatcher;
import com.kett.TicketSystem.util.RestRequestHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles({ "test" })
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class ProjectControllerTests {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final EventCatcher eventCatcher;
    private final ApplicationEventPublisher eventPublisher;
    private final ProjectDomainService projectDomainService;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final RestRequestHelper restMinion;

    private UUID userId;
    private String userName;
    private String userEmail;
    private String userPassword;
    private String jwt;

    private UUID projectId;
    private String projectName;
    private String projectDescription;

    private UUID buildUpProjectId;
    private String buildUpProjectName;
    private String buildUpProjectDescription;

    @Autowired
    public ProjectControllerTests(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            EventCatcher eventCatcher,
            ApplicationEventPublisher eventPublisher,
            ProjectDomainService projectDomainService,
            ProjectRepository projectRepository,
            UserRepository userRepository
    ) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.eventCatcher = eventCatcher;
        this.eventPublisher = eventPublisher;
        this.projectDomainService = projectDomainService;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.restMinion = new RestRequestHelper(this.mockMvc, this.objectMapper);
    }

    @BeforeEach
    public void buildUp() throws Exception {
        userName = "Franz Gerald Stauffingen";
        userEmail = "franz.stauffingen@gmail.com";
        userPassword = "MyLittlePony4Ever!";
        userId = restMinion.postUser(userName, userEmail, userPassword);
        jwt = restMinion.authenticateUser(userEmail, userPassword);

        buildUpProjectName = "Mozzarella";
        buildUpProjectDescription = "What do you do with the white ball after drinking the mozzarella?";
        buildUpProjectId = restMinion.postProject(jwt, buildUpProjectName, buildUpProjectDescription);

        projectName = "My Little Project";
        projectDescription = "My very own project description";
    }

    @AfterEach
    public void tearDown() {
        jwt = null;
        userId = null;
        userName = null;
        userEmail = null;
        userPassword = null;

        projectId = null;
        projectName = null;
        projectDescription = null;

        buildUpProjectId = null;
        buildUpProjectName = null;
        buildUpProjectDescription = null;

        projectRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void postProjectTest() throws Exception {
        eventCatcher.catchEventOfType(ProjectCreatedEvent.class);
        ProjectPostDto projectPostDto = new ProjectPostDto(projectName, projectDescription);
        MvcResult postResult =
                mockMvc.perform(
                                post("/projects")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectPostDto))
                                        .header("Authorization", jwt))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.name").value(projectPostDto.getName()))
                        .andExpect(jsonPath("$.description").value(projectPostDto.getDescription()))
                        .andReturn();

        // test instance
        String postResponse = postResult.getResponse().getContentAsString();
        projectId = UUID.fromString(JsonPath.parse(postResponse).read("$.id"));
        Project project = projectDomainService.getProjectById(projectId);
        assertEquals(projectId, project.getId());
        assertEquals(projectPostDto.getName(), project.getName());
        assertEquals(projectPostDto.getDescription(), project.getDescription());

        // test ProjectCreatedEvent
        await().until(eventCatcher::hasCaughtEvent);
        ProjectCreatedEvent projectCreatedEvent = (ProjectCreatedEvent) eventCatcher.getEvent();
        assertEquals(projectId, projectCreatedEvent.getProjectId());
        assertEquals(userId, projectCreatedEvent.getUserId());
    }

    @Test
    public void getProjectTest() throws Exception {
        MvcResult getResult =
                mockMvc.perform(
                                get("/projects/" + buildUpProjectId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", jwt))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(buildUpProjectId.toString()))
                        .andExpect(jsonPath("$.name").value(buildUpProjectName))
                        .andExpect(jsonPath("$.description").value(buildUpProjectDescription))
                        .andReturn();
    }

    @Test
    public void getNonExistingProjectTest() throws Exception {
        MvcResult getResult =
                mockMvc.perform(
                                get("/projects/" + UUID.randomUUID())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", jwt))
                        .andExpect(status().isForbidden())
                        .andReturn();
    }

    @Test
    public void deleteProjectTest() throws Exception {
        eventCatcher.catchEventOfType(ProjectDeletedEvent.class);
        MvcResult deleteResult =
                mockMvc.perform(
                        delete("/projects/" + buildUpProjectId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", jwt))
                .andExpect(status().isNoContent())
                .andReturn();

        // test ProjectDeletedEvent
        await().until(eventCatcher::hasCaughtEvent);
        ProjectDeletedEvent projectDeletedEvent = (ProjectDeletedEvent) eventCatcher.getEvent();
        assertEquals(buildUpProjectId, projectDeletedEvent.getProjectId());

        // test instance
        assertThrows(NoProjectFoundException.class, () -> projectDomainService.getProjectById(buildUpProjectId));
    }

    @Test
    public void patchProjectTest() throws Exception {
        String newName = "Hallo";
        String newDescription = "Ciao";
        ProjectPatchDto projectPatchDto = new ProjectPatchDto(newName, newDescription);
        MvcResult patchResult =
                mockMvc.perform(
                                patch("/projects/" + buildUpProjectId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectPatchDto))
                                        .header("Authorization", jwt))
                        .andExpect(status().isNoContent())
                        .andReturn();

        Project project = projectDomainService.getProjectById(buildUpProjectId);
        assertEquals(buildUpProjectId, project.getId());
        assertEquals(projectPatchDto.getName(), project.getName());
        assertEquals(projectPatchDto.getDescription(), project.getDescription());
    }

    @Test
    public void consumeUserCreatedEventTest() throws Exception {
        UUID mockUserId = UUID.randomUUID();
        String mockUserName = "Stefan Stephens";
        EmailAddress mockEmailAddress = EmailAddress.fromString("Stef.steph@timeless.de");

        eventCatcher.catchEventOfType(DefaultProjectCreatedEvent.class);
        eventPublisher.publishEvent(
                new UserCreatedEvent(
                        mockUserId,
                        mockUserName,
                        mockEmailAddress
                )
        );

        // test DefaultProjectCreatedEvent
        await().until(eventCatcher::hasCaughtEvent);
        DefaultProjectCreatedEvent defaultProjectCreatedEvent = (DefaultProjectCreatedEvent) eventCatcher.getEvent();
        assertEquals(mockUserId, defaultProjectCreatedEvent.getUserId());

        // test if project was actually created
        Project defaultProject = projectDomainService.getProjectById(defaultProjectCreatedEvent.getProjectId());
    }

    @Test
    public void consumeLastMembershipDeletedEventTest() throws Exception {
        eventCatcher.catchEventOfType(ProjectDeletedEvent.class);
        eventPublisher.publishEvent(
                new LastProjectMemberDeletedEvent(
                        UUID.randomUUID(),
                        userId,
                        buildUpProjectId
                )
        );

        // test DefaultProjectCreatedEvent
        await().until(eventCatcher::hasCaughtEvent);
        ProjectDeletedEvent projectDeletedEvent = (ProjectDeletedEvent) eventCatcher.getEvent();
        assertEquals(buildUpProjectId, projectDeletedEvent.getProjectId());

        // test instance
        assertThrows(NoProjectFoundException.class, () -> projectDomainService.getProjectById(buildUpProjectId));
    }
}
