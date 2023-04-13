package com.kett.TicketSystem.phase.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.kett.TicketSystem.phase.application.dto.PhasePostDto;
import com.kett.TicketSystem.phase.domain.Phase;
import com.kett.TicketSystem.phase.domain.events.PhaseCreatedEvent;
import com.kett.TicketSystem.phase.domain.events.PhaseDeletedEvent;
import com.kett.TicketSystem.phase.domain.exceptions.NoPhaseFoundException;
import com.kett.TicketSystem.phase.repository.PhaseRepository;
import com.kett.TicketSystem.project.repository.ProjectRepository;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import java.util.List;
import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private final EventCatcher eventCatcher;
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
            EventCatcher eventCatcher,
            PhaseService phaseService,
            PhaseRepository phaseRepository,
            ProjectRepository projectRepository,
            UserRepository userRepository
    ) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.eventCatcher = eventCatcher;
        this.restMinion = new RestRequestHelper(mockMvc, objectMapper);
        this.eventPublisher = eventPublisher;
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
        eventCatcher.catchEventOfType(PhaseCreatedEvent.class);
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
        await().until(eventCatcher::hasCaughtEvent);
        PhaseCreatedEvent phaseCreatedEvent = (PhaseCreatedEvent) eventCatcher.getEvent();
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
        eventCatcher.catchEventOfType(PhaseCreatedEvent.class);
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
        await().until(eventCatcher::hasCaughtEvent);
        PhaseCreatedEvent phaseCreatedEvent1 = (PhaseCreatedEvent) eventCatcher.getEvent();
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

    @Test
    public void deletePhaseTest() throws Exception {
        restMinion.postPhase(jwt, buildUpProjectId, phaseName1, null);
        restMinion.postPhase(jwt, buildUpProjectId, phaseName0, null);

        // test initial state
        List<Phase> initialPhases = phaseService.getPhasesByProjectId(buildUpProjectId);
        assertEquals(3, initialPhases.size());

        assertEquals("BACKLOG", initialPhases.get(0).getName());
        UUID backlogId = initialPhases.get(0).getId();
        assertFalse(initialPhases.get(0).isFirst());
        assertTrue(initialPhases.get(0).isLast());
        assertEquals(initialPhases.get(1).getId(), initialPhases.get(0).getPreviousPhase().getId());
        assertNull(initialPhases.get(0).getNextPhase());

        assertEquals(phaseName1, initialPhases.get(1).getName());
        UUID phaseId1 = initialPhases.get(1).getId();
        assertFalse(initialPhases.get(1).isFirst());
        assertFalse(initialPhases.get(1).isLast());
        assertEquals(initialPhases.get(2).getId(), initialPhases.get(1).getPreviousPhase().getId());
        assertEquals(initialPhases.get(0).getId(), initialPhases.get(1).getNextPhase().getId());

        assertEquals(phaseName0, initialPhases.get(2).getName());
        UUID phaseId0 = initialPhases.get(2).getId();
        assertTrue(initialPhases.get(2).isFirst());
        assertFalse(initialPhases.get(2).isLast());
        assertNull(initialPhases.get(2).getPreviousPhase());
        assertEquals(initialPhases.get(1).getId(), initialPhases.get(2).getNextPhase().getId());

        // delete middle -> phaseId1
        eventCatcher.catchEventOfType(PhaseDeletedEvent.class);
        MvcResult postResult0 =
                mockMvc.perform(
                                delete("/phases/" + phaseId1)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .cookie(new Cookie("jwt", jwt)))
                        .andExpect(status().isNoContent())
                        .andReturn();

        // test event
        await().until(eventCatcher::hasCaughtEvent);
        PhaseDeletedEvent phaseDeletedEvent = (PhaseDeletedEvent) eventCatcher.getEvent();
        assertEquals(phaseId1, phaseDeletedEvent.getPhaseId());
        assertEquals(buildUpProjectId, phaseDeletedEvent.getProjectId());

        // test if phase was actually deleted
        assertThrows(NoPhaseFoundException.class, () -> phaseService.getPhaseById(phaseId1));

        // test other phases after delete
        List<Phase> phasesAfterFirstDelete = phaseService.getPhasesByProjectId(buildUpProjectId);
        assertEquals(2, phasesAfterFirstDelete.size());
        assertEquals(backlogId, phasesAfterFirstDelete.get(0).getId());
        assertTrue(phasesAfterFirstDelete.get(0).isLast());
        assertEquals(phasesAfterFirstDelete.get(0).getPreviousPhase().getId(), phasesAfterFirstDelete.get(1).getId());
        assertNull(phasesAfterFirstDelete.get(0).getNextPhase());
        assertEquals(phaseId0, phasesAfterFirstDelete.get(1).getId());
        assertTrue(phasesAfterFirstDelete.get(1).isFirst());
        assertEquals(phasesAfterFirstDelete.get(1).getNextPhase().getId(), phasesAfterFirstDelete.get(0).getId());
        assertNull(phasesAfterFirstDelete.get(1).getPreviousPhase());

        // TODO: test next two deletes
    }
}
