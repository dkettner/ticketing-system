package com.kett.TicketSystem.phase.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.kett.TicketSystem.phase.application.dto.PhasePostDto;
import com.kett.TicketSystem.phase.domain.Phase;
import com.kett.TicketSystem.phase.domain.events.PhaseCreatedEvent;
import com.kett.TicketSystem.phase.repository.PhaseRepository;
import com.kett.TicketSystem.project.repository.ProjectRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class PhaseControllerTests {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final RestRequestHelper restMinion;
    private final ApplicationEventPublisher eventPublisher;
    private final DummyEventListener dummyEventListener;
    private final PhaseService phaseService;
    private final PhaseRepository phaseRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    private String userName;
    private String userEmail;
    private String userPassword;
    private UUID userId;
    private String jwt;

    private String buildUpProjectName;
    private String buildUpProjectDescription;
    private UUID buildUpProjectId;

    private String differentProjectDescription;
    private String differentProjectName;

    private String phaseName0;
    private String phaseName1;
    private String phaseName2;
    private String phaseName3;

    @Autowired
    public PhaseControllerTests(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            ApplicationEventPublisher eventPublisher,
            DummyEventListener dummyEventListener,
            PhaseService phaseService,
            PhaseRepository phaseRepository,
            ProjectRepository projectRepository,
            UserRepository userRepository
    ) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.restMinion = new RestRequestHelper(mockMvc, objectMapper);
        this.eventPublisher = eventPublisher;
        this.dummyEventListener = dummyEventListener;
        this.phaseService = phaseService;
        this.phaseRepository = phaseRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @BeforeEach
    public void buildUp() throws Exception {
        userName = "Geralt";
        userEmail = "il.brucho@netflix.com";
        userPassword = "DiesDasAnanasiospjefosias9999023";
        userId = restMinion.postUser(userName, userEmail, userPassword);
        jwt = restMinion.authenticateUser(userEmail, userPassword);

        buildUpProjectName = "toss a coin to your witcher";
        buildUpProjectDescription = "50ct please";
        buildUpProjectId = restMinion.postProject(jwt, buildUpProjectName, buildUpProjectDescription);

        differentProjectName = "Stormcloaks";
        differentProjectDescription = "Not the Imperial Legion.";

        phaseName0 = "phaseName0";
        phaseName1 = "phaseName1";
        phaseName2 = "phaseName2";
        phaseName3 = "phaseName3";

        dummyEventListener.deleteAllEvents();
    }

    @AfterEach
    public void tearDown() {
        userName = null;
        userEmail = null;
        userPassword = null;
        userId = null;
        jwt = null;

        buildUpProjectName = null;
        buildUpProjectDescription = null;
        buildUpProjectId = null;

        differentProjectName = null;
        differentProjectDescription = null;

        phaseName0 = null;
        phaseName1 = null;
        phaseName2 = null;
        phaseName3 = null;

        phaseRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();
    }
    @Test
    public void getPhaseByIdTest() throws Exception {
        UUID phaseId = restMinion.postPhase(jwt, buildUpProjectId, phaseName0, null);
        Phase phase = phaseService.getPhaseById(phaseId);
        MvcResult getResult =
                mockMvc.perform(
                                get("/phases/" + phaseId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .cookie(new Cookie("jwt", jwt)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(phaseId.toString()))
                        .andExpect(jsonPath("$.projectId").value(buildUpProjectId.toString()))
                        .andExpect(jsonPath("$.name").value(phaseName0))
                        .andExpect(jsonPath("$.previousPhaseId").isEmpty())
                        .andExpect(jsonPath("$.nextPhaseId").exists())
                        .andExpect(jsonPath("$.ticketCount").value(0))
                        .andReturn();
    }

    @Test
    public void getPhasesByQueryTest() throws Exception {
        UUID phaseId = restMinion.postPhase(jwt, buildUpProjectId, phaseName0, null);
        List<Phase> phases = phaseService.getPhasesByProjectId(buildUpProjectId);

        // defaultPhase + new phase = 2
        MvcResult getResult =
                mockMvc.perform(
                                get("/phases")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .queryParam("project-id", buildUpProjectId.toString())
                                        .cookie(new Cookie("jwt", jwt)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$").isArray())
                        .andExpect(jsonPath("$[0].id").value(phases.get(0).getId().toString()))
                        .andExpect(jsonPath("$[0].projectId").value(phases.get(0).getProjectId().toString()))
                        .andExpect(jsonPath("$[0].name").value(phases.get(0).getName()))
                        .andExpect(jsonPath("$[0].previousPhaseId").value(phases.get(0).getPreviousPhase().getId().toString()))
                        .andExpect(jsonPath("$[0].nextPhaseId").isEmpty())
                        .andExpect(jsonPath("$[0].ticketCount").value(phases.get(0).getTicketCount()))
                        .andExpect(jsonPath("$[1].id").value(phases.get(1).getId().toString()))
                        .andExpect(jsonPath("$[1].projectId").value(phases.get(1).getProjectId().toString()))
                        .andExpect(jsonPath("$[1].name").value(phases.get(1).getName()))
                        .andExpect(jsonPath("$[1].previousPhaseId").isEmpty())
                        .andExpect(jsonPath("$[1].nextPhaseId").value(phases.get(1).getNextPhase().getId().toString()))
                        .andExpect(jsonPath("$[1].ticketCount").value(phases.get(1).getTicketCount()))
                        .andReturn();
    }

    @Test
    public void postPhaseToNewProjectTest() throws Exception {
        // post to first place
        PhasePostDto phasePostDto0 = new PhasePostDto(buildUpProjectId, phaseName0, null);
        MvcResult postResult0 =
                mockMvc.perform(
                                post("/phases")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(phasePostDto0))
                                        .cookie(new Cookie("jwt", jwt)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.projectId").value(buildUpProjectId.toString()))
                        .andExpect(jsonPath("$.name").value(phaseName0))
                        .andExpect(jsonPath("$.previousPhaseId").isEmpty())
                        .andExpect(jsonPath("$.nextPhaseId").exists())
                        .andExpect(jsonPath("$.ticketCount").value(0))
                        .andReturn();
        String postResponse0 = postResult0.getResponse().getContentAsString();
        UUID phaseId0 = UUID.fromString(JsonPath.parse(postResponse0).read("$.id"));
        UUID nextPhaseId = UUID.fromString(JsonPath.parse(postResponse0).read("$.nextPhaseId"));

        // test event
        Optional<PhaseCreatedEvent> event = dummyEventListener.getLatestPhaseCreatedEvent();
        assertTrue(event.isPresent());
        Optional<PhaseCreatedEvent> emptyEvent = dummyEventListener.getLatestPhaseCreatedEvent();
        assertTrue(emptyEvent.isEmpty()); // check if only one event was thrown
        PhaseCreatedEvent phaseCreatedEvent = event.get();
        assertEquals(phaseId0, phaseCreatedEvent.getPhaseId());
        assertEquals(phasePostDto0.getProjectId(), phaseCreatedEvent.getProjectId());

        // test instance
        Phase phase0 = phaseService.getPhaseById(phaseId0);
        assertEquals(phaseId0, phase0.getId());
        assertEquals(phasePostDto0.getProjectId(), phase0.getProjectId());
        assertEquals(phasePostDto0.getName(), phase0.getName());
        assertNull(phase0.getPreviousPhase());
        assertEquals(nextPhaseId, phase0.getNextPhase().getId());

        // post to second place
        PhasePostDto phasePostDto1 = new PhasePostDto(buildUpProjectId, phaseName1, phaseId0);
        MvcResult postResult1 =
                mockMvc.perform(
                                post("/phases")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(phasePostDto1))
                                        .cookie(new Cookie("jwt", jwt)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.projectId").value(buildUpProjectId.toString()))
                        .andExpect(jsonPath("$.name").value(phaseName1))
                        .andExpect(jsonPath("$.previousPhaseId").value(phaseId0.toString()))
                        .andExpect(jsonPath("$.nextPhaseId").value(nextPhaseId.toString()))
                        .andExpect(jsonPath("$.ticketCount").value(0))
                        .andReturn();

        String postResponse1 = postResult1.getResponse().getContentAsString();
        UUID phaseId1 = UUID.fromString(JsonPath.parse(postResponse1).read("$.id"));

        // test event
        Optional<PhaseCreatedEvent> event1 = dummyEventListener.getLatestPhaseCreatedEvent();
        assertTrue(event1.isPresent());
        Optional<PhaseCreatedEvent> emptyEvent1 = dummyEventListener.getLatestPhaseCreatedEvent();
        assertTrue(emptyEvent1.isEmpty()); // check if only one event was thrown
        PhaseCreatedEvent phaseCreatedEvent1 = event1.get();
        assertEquals(phaseId1, phaseCreatedEvent1.getPhaseId());
        assertEquals(phasePostDto1.getProjectId(), phaseCreatedEvent1.getProjectId());

        // test instance
        Phase phase1 = phaseService.getPhaseById(phaseId1);
        assertEquals(phaseId1, phase1.getId());
        assertEquals(phasePostDto1.getProjectId(), phase1.getProjectId());
        assertEquals(phasePostDto1.getName(), phase1.getName());
        assertEquals(phasePostDto1.getPreviousPhaseId(), phase1.getPreviousPhase().getId());
        assertEquals(nextPhaseId, phase1.getNextPhase().getId());
    }


}
