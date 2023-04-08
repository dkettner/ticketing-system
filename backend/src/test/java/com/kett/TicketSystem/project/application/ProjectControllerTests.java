package com.kett.TicketSystem.project.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.kett.TicketSystem.authentication.dto.AuthenticationPostDto;
import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import com.kett.TicketSystem.membership.domain.events.LastProjectMemberDeletedEvent;
import com.kett.TicketSystem.project.application.dto.ProjectPostDto;
import com.kett.TicketSystem.project.domain.Project;
import com.kett.TicketSystem.project.domain.events.DefaultProjectCreatedEvent;
import com.kett.TicketSystem.project.domain.events.ProjectCreatedEvent;
import com.kett.TicketSystem.project.repository.ProjectRepository;
import com.kett.TicketSystem.user.application.dto.UserPostDto;
import com.kett.TicketSystem.user.domain.User;
import com.kett.TicketSystem.user.domain.events.UserCreatedEvent;
import com.kett.TicketSystem.util.DummyEventListener;
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

import javax.servlet.http.Cookie;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProjectControllerTests {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final DummyEventListener dummyEventListener;
    private final ApplicationEventPublisher eventPublisher;
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;

    private String userId;
    private String userName;
    private String userEmail;
    private String userPassword;
    private String jwt;

    private String projectId;
    private String projectName;
    private String projectDescription;



    @Autowired
    public ProjectControllerTests(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            DummyEventListener dummyEventListener,
            ApplicationEventPublisher eventPublisher,
            ProjectService projectService,
            ProjectRepository projectRepository
    ) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.dummyEventListener = dummyEventListener;
        this.eventPublisher = eventPublisher;
        this.projectService = projectService;
        this.projectRepository = projectRepository;
    }

    @BeforeEach
    public void buildUp() throws Exception {
        userName = "Franz Gerald Stauffingen";
        userEmail = "franz.stauffingen@gmail.com";
        userPassword = "MyLittlePony4Ever!";

        projectName = "My Little Project";
        projectDescription = "My very own project description";

        // post user
        UserPostDto userPostDto = new UserPostDto(userName, userEmail, userPassword);
        MvcResult userPostResult =
                mockMvc.perform(
                                post("/users")
                                        .content(objectMapper.writeValueAsString(userPostDto))
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn();
        String dummyResponse = userPostResult.getResponse().getContentAsString();
        userId = JsonPath.parse(dummyResponse).read("$.id");

        // authenticate user
        AuthenticationPostDto authenticationPostDto4 = new AuthenticationPostDto(userEmail, userPassword);
        MvcResult postAuthenticationResult4 =
                mockMvc.perform(
                                post("/authentication")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(authenticationPostDto4)))
                        .andExpect(status().isOk())
                        .andReturn();
        jwt = Objects.requireNonNull(postAuthenticationResult4.getResponse().getCookie("jwt")).getValue();

        dummyEventListener.deleteAllEvents(); // in buildUp to erase events of buildUp

        // post Project
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

        projectRepository.deleteAll();
    }

    @Test
    public void postProjectTest() throws Exception {

        // post project
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
        projectId = JsonPath.parse(postResponse).read("$.id");
        Project project = projectService.getProjectById(UUID.fromString(projectId));
        assertEquals(UUID.fromString(projectId), project.getId());
        assertEquals(projectPostDto.getName(), project.getName());
        assertEquals(projectPostDto.getDescription(), project.getDescription());

        // test ProjectCreatedEvent
        Optional<ProjectCreatedEvent> event = dummyEventListener.getLatestProjectCreatedEvent();
        assertTrue(event.isPresent());
        Optional<ProjectCreatedEvent> emptyEvent = dummyEventListener.getLatestProjectCreatedEvent();
        assertTrue(emptyEvent.isEmpty()); // check if only one event was thrown

        ProjectCreatedEvent projectCreatedEvent = event.get();
        assertEquals(UUID.fromString(projectId), projectCreatedEvent.getProjectId());
        assertEquals(UUID.fromString(userId), projectCreatedEvent.getUserId());
    }

    @Test
    public void consumeUserCreatedEventTest() throws Exception {
        eventPublisher.publishEvent(
                new UserCreatedEvent(
                        UUID.fromString(userId),
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
        assertEquals(UUID.fromString(userId), defaultProjectCreatedEvent.getUserId());

        // test if project was actually created
        Project defaultProject = projectService.getProjectById(defaultProjectCreatedEvent.getProjectId());
    }

    /*
    @Test
    public void consumeLastMembershipDeletedEventTest() throws Exception {
        eventPublisher.publishEvent(
                new LastProjectMemberDeletedEvent(
                        UUID.randomUUID(),
                        projectId;
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
        assertEquals(UUID.fromString(userId), defaultProjectCreatedEvent.getUserId());

        // test if project was actually created
        Project defaultProject = projectService.getProjectById(defaultProjectCreatedEvent.getProjectId());
    }
    */

}
