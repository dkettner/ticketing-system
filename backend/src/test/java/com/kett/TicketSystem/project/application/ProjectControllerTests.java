package com.kett.TicketSystem.project.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import com.kett.TicketSystem.common.exceptions.NoProjectFoundException;
import com.kett.TicketSystem.membership.domain.events.LastProjectMemberDeletedEvent;
import com.kett.TicketSystem.project.application.dto.ProjectPatchDto;
import com.kett.TicketSystem.project.application.dto.ProjectPostDto;
import com.kett.TicketSystem.project.domain.Project;
import com.kett.TicketSystem.project.domain.events.DefaultProjectCreatedEvent;
import com.kett.TicketSystem.project.domain.events.ProjectCreatedEvent;
import com.kett.TicketSystem.project.domain.events.ProjectDeletedEvent;
import com.kett.TicketSystem.project.repository.ProjectRepository;
import com.kett.TicketSystem.user.domain.events.UserCreatedEvent;
import com.kett.TicketSystem.user.repository.UserRepository;
import com.kett.TicketSystem.util.DummyEventListener;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class ProjectControllerTests {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final DummyEventListener dummyEventListener;
    private final ApplicationEventPublisher eventPublisher;
    private final ProjectService projectService;
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
            DummyEventListener dummyEventListener,
            ApplicationEventPublisher eventPublisher,
            ProjectService projectService,
            ProjectRepository projectRepository,
            UserRepository userRepository
    ) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.dummyEventListener = dummyEventListener;
        this.eventPublisher = eventPublisher;
        this.projectService = projectService;
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

        dummyEventListener.deleteAllEvents(); // erase events of buildUp
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
        ProjectPostDto projectPostDto = new ProjectPostDto(projectName, projectDescription);
        MvcResult postResult =
                mockMvc.perform(
                                post("/projects")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectPostDto))
                                        .cookie(new Cookie("jwt", jwt)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.name").value(projectPostDto.getName()))
                        .andExpect(jsonPath("$.description").value(projectPostDto.getDescription()))
                        .andReturn();
        String postResponse = postResult.getResponse().getContentAsString();
        projectId = UUID.fromString(JsonPath.parse(postResponse).read("$.id"));
        Project project = projectService.getProjectById(projectId);
        assertEquals(projectId, project.getId());
        assertEquals(projectPostDto.getName(), project.getName());
        assertEquals(projectPostDto.getDescription(), project.getDescription());

        // test ProjectCreatedEvent
        Optional<ProjectCreatedEvent> event = dummyEventListener.getLatestProjectCreatedEvent();
        assertTrue(event.isPresent());
        Optional<ProjectCreatedEvent> emptyEvent = dummyEventListener.getLatestProjectCreatedEvent();
        assertTrue(emptyEvent.isEmpty()); // check if only one event was thrown

        ProjectCreatedEvent projectCreatedEvent = event.get();
        assertEquals(projectId, projectCreatedEvent.getProjectId());
        assertEquals(userId, projectCreatedEvent.getUserId());
    }

    @Test
    public void getProjectTest() throws Exception {
        MvcResult getResult =
                mockMvc.perform(
                                get("/projects/" + buildUpProjectId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .cookie(new Cookie("jwt", jwt)))
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
                                        .cookie(new Cookie("jwt", jwt)))
                        .andExpect(status().isForbidden())
                        .andReturn();
    }

    @Test
    public void deleteProjectTest() throws Exception {
        MvcResult deleteResult =
                mockMvc.perform(
                        delete("/projects/" + buildUpProjectId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .cookie(new Cookie("jwt", jwt)))
                .andExpect(status().isNoContent())
                .andReturn();

        // test ProjectDeletedEvent
        Optional<ProjectDeletedEvent> event = dummyEventListener.getLatestProjectDeletedEvent();
        assertTrue(event.isPresent());
        Optional<ProjectDeletedEvent> emptyEvent = dummyEventListener.getLatestProjectDeletedEvent();
        assertTrue(emptyEvent.isEmpty()); // check if only one event was thrown

        ProjectDeletedEvent projectDeletedEvent = event.get();
        assertEquals(buildUpProjectId, projectDeletedEvent.getProjectId());

        try {
            projectService.getProjectById(buildUpProjectId);
            fail("Project was found but should have been deleted");
        } catch (NoProjectFoundException exception) {
            // test passed
        }
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
                                        .cookie(new Cookie("jwt", jwt)))
                        .andExpect(status().isNoContent())
                        .andReturn();

        Project project = projectService.getProjectById(buildUpProjectId);
        assertEquals(buildUpProjectId, project.getId());
        assertEquals(projectPatchDto.getName(), project.getName());
        assertEquals(projectPatchDto.getDescription(), project.getDescription());
    }

    @Test
    public void consumeUserCreatedEventTest() throws Exception {
        eventPublisher.publishEvent(
                new UserCreatedEvent(
                        userId,
                        userName,
                        EmailAddress.fromString(userEmail)
                )
        );

        // TODO: find more stable alternative for testing
        // shame: give services time to handle UserCreatedEvents
        Thread.sleep(100);

        // test DefaultProjectCreatedEvent
        Optional<DefaultProjectCreatedEvent> event0 = dummyEventListener.getLatestDefaultProjectCreatedEvent();
        assertTrue(event0.isPresent());
        Optional<DefaultProjectCreatedEvent> emptyEvent0 = dummyEventListener.getLatestDefaultProjectCreatedEvent();
        assertTrue(emptyEvent0.isEmpty()); // check if only one event was thrown

        DefaultProjectCreatedEvent defaultProjectCreatedEvent = event0.get();
        assertEquals(userId, defaultProjectCreatedEvent.getUserId());

        // test if project was actually created
        Project defaultProject = projectService.getProjectById(defaultProjectCreatedEvent.getProjectId());
    }

    @Test
    public void consumeLastMembershipDeletedEventTest() throws Exception {
        eventPublisher.publishEvent(
                new LastProjectMemberDeletedEvent(
                        userId,
                        buildUpProjectId
                )
        );

        // TODO: find more stable alternative for testing
        // shame: give services time to handle UserCreatedEvents
        Thread.sleep(100);

        // test DefaultProjectCreatedEvent
        Optional<ProjectDeletedEvent> event = dummyEventListener.getLatestProjectDeletedEvent();
        assertTrue(event.isPresent());
        Optional<ProjectDeletedEvent> emptyEvent = dummyEventListener.getLatestProjectDeletedEvent();
        assertTrue(emptyEvent.isEmpty()); // check if only one event was thrown

        ProjectDeletedEvent projectDeletedEvent = event.get();
        assertEquals(buildUpProjectId, projectDeletedEvent.getProjectId());

        try {
            projectService.getProjectById(buildUpProjectId);
            fail("Project was found but should have been deleted");
        } catch (NoProjectFoundException exception) {
            // test passed
        }
    }
}