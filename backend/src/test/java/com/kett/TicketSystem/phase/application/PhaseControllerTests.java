package com.kett.TicketSystem.phase.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.kett.TicketSystem.phase.application.dto.PhasePutNameDto;
import com.kett.TicketSystem.phase.application.dto.PhasePutPositionDto;
import com.kett.TicketSystem.phase.application.dto.PhasePostDto;
import com.kett.TicketSystem.phase.domain.Phase;
import com.kett.TicketSystem.phase.domain.PhaseDomainService;
import com.kett.TicketSystem.phase.domain.events.PhaseCreatedEvent;
import com.kett.TicketSystem.phase.domain.events.PhaseDeletedEvent;
import com.kett.TicketSystem.phase.domain.exceptions.NoPhaseFoundException;
import com.kett.TicketSystem.phase.repository.PhaseRepository;
import com.kett.TicketSystem.project.domain.events.DefaultProjectCreatedEvent;
import com.kett.TicketSystem.project.domain.events.ProjectCreatedEvent;
import com.kett.TicketSystem.project.domain.events.ProjectDeletedEvent;
import com.kett.TicketSystem.project.repository.ProjectRepository;
import com.kett.TicketSystem.ticket.domain.events.TicketCreatedEvent;
import com.kett.TicketSystem.ticket.domain.events.TicketDeletedEvent;
import com.kett.TicketSystem.ticket.domain.events.TicketPhaseUpdatedEvent;
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
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
public class PhaseControllerTests {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final RestRequestHelper restMinion;
    private final ApplicationEventPublisher eventPublisher;
    private final EventCatcher eventCatcher;
    private final PhaseDomainService phaseDomainService;
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
            PhaseDomainService phaseDomainService,
            PhaseRepository phaseRepository,
            ProjectRepository projectRepository,
            UserRepository userRepository
    ) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.eventCatcher = eventCatcher;
        this.restMinion = new RestRequestHelper(mockMvc, objectMapper);
        this.eventPublisher = eventPublisher;
        this.phaseDomainService = phaseDomainService;
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
        Phase phase = phaseDomainService.getPhaseById(phaseId);
        MvcResult getResult =
                mockMvc.perform(
                                get("/phases/" + phaseId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", jwt))
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
        List<Phase> phases = phaseDomainService.getPhasesByProjectId(buildUpProjectId);

        // defaultPhase + new phase = 2
        MvcResult getResult =
                mockMvc.perform(
                                get("/phases")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .queryParam("project-id", buildUpProjectId.toString())
                                        .header("Authorization", jwt))
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
                                        .header("Authorization", jwt))
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
        Phase phase0 = phaseDomainService.getPhaseById(phaseId0);
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
                                        .header("Authorization", jwt))
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
        Phase phase1 = phaseDomainService.getPhaseById(phaseId1);
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
        List<Phase> initialPhases = phaseDomainService.getPhasesByProjectId(buildUpProjectId);
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
        MvcResult deleteResult0 =
                mockMvc.perform(
                                delete("/phases/" + phaseId1)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", jwt))
                        .andExpect(status().isNoContent())
                        .andReturn();

        // test event
        await().until(eventCatcher::hasCaughtEvent);
        PhaseDeletedEvent phaseDeletedEvent = (PhaseDeletedEvent) eventCatcher.getEvent();
        assertEquals(phaseId1, phaseDeletedEvent.getPhaseId());
        assertEquals(buildUpProjectId, phaseDeletedEvent.getProjectId());

        // test if phase was actually deleted
        assertThrows(NoPhaseFoundException.class, () -> phaseDomainService.getPhaseById(phaseId1));

        // test other phases after delete
        List<Phase> phasesAfterFirstDelete = phaseDomainService.getPhasesByProjectId(buildUpProjectId);
        assertEquals(2, phasesAfterFirstDelete.size());
        assertEquals(backlogId, phasesAfterFirstDelete.get(0).getId());
        assertTrue(phasesAfterFirstDelete.get(0).isLast());
        assertEquals(phasesAfterFirstDelete.get(0).getPreviousPhase().getId(), phasesAfterFirstDelete.get(1).getId());
        assertNull(phasesAfterFirstDelete.get(0).getNextPhase());
        assertEquals(phaseId0, phasesAfterFirstDelete.get(1).getId());
        assertTrue(phasesAfterFirstDelete.get(1).isFirst());
        assertEquals(phasesAfterFirstDelete.get(1).getNextPhase().getId(), phasesAfterFirstDelete.get(0).getId());
        assertNull(phasesAfterFirstDelete.get(1).getPreviousPhase());

        // delete last -> backlogId
        eventCatcher.catchEventOfType(PhaseDeletedEvent.class);
        MvcResult deleteResult1 =
                mockMvc.perform(
                                delete("/phases/" + backlogId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", jwt))
                        .andExpect(status().isNoContent())
                        .andReturn();

        // test event
        await().until(eventCatcher::hasCaughtEvent);
        PhaseDeletedEvent phaseDeletedEvent1 = (PhaseDeletedEvent) eventCatcher.getEvent();
        assertEquals(backlogId, phaseDeletedEvent1.getPhaseId());
        assertEquals(buildUpProjectId, phaseDeletedEvent1.getProjectId());

        // test if phase was actually deleted
        assertThrows(NoPhaseFoundException.class, () -> phaseDomainService.getPhaseById(backlogId));

        // test other phases after delete
        List<Phase> phasesAfterSecondDelete = phaseDomainService.getPhasesByProjectId(buildUpProjectId);
        assertEquals(1, phasesAfterSecondDelete.size());
        assertEquals(phaseId0, phasesAfterSecondDelete.get(0).getId());
        assertTrue(phasesAfterSecondDelete.get(0).isFirst());
        assertTrue(phasesAfterSecondDelete.get(0).isLast());
        assertNull(phasesAfterSecondDelete.get(0).getPreviousPhase());
        assertNull(phasesAfterSecondDelete.get(0).getNextPhase());

        // delete remaining phase -> phaseId0
        eventCatcher.catchEventOfType(PhaseDeletedEvent.class);
        MvcResult deleteResult2 =
                mockMvc.perform(
                                delete("/phases/" + phaseId0)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", jwt))
                        .andExpect(status().isConflict())
                        .andReturn();

        // test event
        try {
            await().atMost(3, TimeUnit.SECONDS).until(eventCatcher::hasCaughtEvent);
            fail();
        } catch (Exception e) {
            // test passed
        }

        // test if phase was actually deleted
        Phase phase = phaseDomainService.getPhaseById(phaseId0);
    }

    @Test
    public void putPhaseNameTest() throws Exception {
        List<Phase> initialPhases = phaseDomainService.getPhasesByProjectId(buildUpProjectId);
        assertEquals(1, initialPhases.size());
        assertEquals("BACKLOG", initialPhases.get(0).getName());
        UUID backlogId = initialPhases.get(0).getId();

        String newName = "Hola que tal";
        PhasePutNameDto phasePutNameDto = new PhasePutNameDto(newName);
        MvcResult putResult =
                mockMvc.perform(
                                put("/phases/" + backlogId + "/name")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(phasePutNameDto))
                                        .header("Authorization", jwt))
                        .andExpect(status().isNoContent())
                        .andReturn();

        // test if name changed
        Phase phase = phaseDomainService.getPhaseById(backlogId);
        assertEquals(backlogId, phase.getId());
        assertEquals(newName, phase.getName());
        assertNotEquals("BACKLOG", phase.getName());
    }

    @Test
    public void putPhasePositionFirstToLastTest() throws Exception {
        restMinion.postPhase(jwt, buildUpProjectId, phaseName1, null);
        restMinion.postPhase(jwt, buildUpProjectId, phaseName0, null);

        // test initial state
        List<Phase> initialPhases = phaseDomainService.getPhasesByProjectId(buildUpProjectId);
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

        // move first to last
        PhasePutPositionDto phasePutPositionDto = new PhasePutPositionDto(backlogId);
        MvcResult putResult =
                mockMvc.perform(
                                put("/phases/" + phaseId0 + "/position")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(phasePutPositionDto))
                                        .header("Authorization", jwt))
                        .andExpect(status().isNoContent())
                        .andReturn();

        // test initial state
        List<Phase> phasesAfterPut = phaseDomainService.getPhasesByProjectId(buildUpProjectId);
        assertEquals(3, phasesAfterPut.size());

        assertEquals("BACKLOG", phasesAfterPut.get(0).getName());
        assertFalse(phasesAfterPut.get(0).isFirst());
        assertFalse(phasesAfterPut.get(0).isLast());
        assertEquals(phasesAfterPut.get(1).getId(), phasesAfterPut.get(0).getPreviousPhase().getId());
        assertEquals(phaseId1, phasesAfterPut.get(0).getPreviousPhase().getId());
        assertEquals(phasesAfterPut.get(2).getId(), phasesAfterPut.get(0).getNextPhase().getId());
        assertEquals(phaseId0, phasesAfterPut.get(0).getNextPhase().getId());

        assertEquals(phaseName1, phasesAfterPut.get(1).getName());
        assertTrue(phasesAfterPut.get(1).isFirst());
        assertFalse(phasesAfterPut.get(1).isLast());
        assertNull(phasesAfterPut.get(1).getPreviousPhase());
        assertEquals(phasesAfterPut.get(0).getId(), phasesAfterPut.get(1).getNextPhase().getId());
        assertEquals(backlogId, phasesAfterPut.get(1).getNextPhase().getId());

        assertEquals(phaseName0, phasesAfterPut.get(2).getName());
        assertFalse(phasesAfterPut.get(2).isFirst());
        assertTrue(phasesAfterPut.get(2).isLast());
        assertEquals(phasesAfterPut.get(0).getId(), phasesAfterPut.get(2).getPreviousPhase().getId());
        assertEquals(backlogId, phasesAfterPut.get(2).getPreviousPhase().getId());
        assertNull(phasesAfterPut.get(2).getNextPhase());
    }

    @Test
    public void consumeDefaultProjectCreatedEventTest() throws Exception {
        UUID tempProjectId = UUID.randomUUID();

        eventCatcher.catchEventOfType(PhaseCreatedEvent.class);
        eventPublisher.publishEvent(new DefaultProjectCreatedEvent(tempProjectId, userId));
        Thread.sleep(100); // give enough time to handle event

        // test at least one PhaseCreatedEvent
        await().until(eventCatcher::hasCaughtEvent);
        PhaseCreatedEvent phaseCreatedEvent = (PhaseCreatedEvent) eventCatcher.getEvent();
        assertEquals(tempProjectId, phaseCreatedEvent.getProjectId());

        // test phases
        List<Phase> phases = phaseDomainService.getPhasesByProjectId(tempProjectId);
        assertEquals(4, phases.size());

        assertEquals("DONE", phases.get(0).getName());
        UUID doneId = phases.get(0).getId();
        assertFalse(phases.get(0).isFirst());
        assertTrue(phases.get(0).isLast());
        assertEquals(phases.get(1).getId(), phases.get(0).getPreviousPhase().getId());
        assertNull(phases.get(0).getNextPhase());

        assertEquals("REVIEW", phases.get(1).getName());
        UUID reviewId = phases.get(1).getId();
        assertFalse(phases.get(1).isFirst());
        assertFalse(phases.get(1).isLast());
        assertEquals(phases.get(2).getId(), phases.get(1).getPreviousPhase().getId());
        assertEquals(phases.get(0).getId(), phases.get(1).getNextPhase().getId());

        assertEquals("DOING", phases.get(2).getName());
        UUID doingId = phases.get(2).getId();
        assertFalse(phases.get(2).isFirst());
        assertFalse(phases.get(2).isLast());
        assertEquals(phases.get(3).getId(), phases.get(2).getPreviousPhase().getId());
        assertEquals(phases.get(1).getId(), phases.get(2).getNextPhase().getId());

        assertEquals("BACKLOG", phases.get(3).getName());
        UUID backlogId = phases.get(3).getId();
        assertTrue(phases.get(3).isFirst());
        assertFalse(phases.get(3).isLast());
        assertNull(phases.get(3).getPreviousPhase());
        assertEquals(phases.get(2).getId(), phases.get(3).getNextPhase().getId());
    }

    @Test
    public void consumeProjectCreatedEventTest() throws Exception {
        UUID tempProjectId = UUID.randomUUID();

        eventCatcher.catchEventOfType(PhaseCreatedEvent.class);
        eventPublisher.publishEvent(new ProjectCreatedEvent(tempProjectId, userId));
        Thread.sleep(100); // give enough time to handle event

        // test event
        await().until(eventCatcher::hasCaughtEvent);
        PhaseCreatedEvent phaseCreatedEvent = (PhaseCreatedEvent) eventCatcher.getEvent();
        assertEquals(tempProjectId, phaseCreatedEvent.getProjectId());

        // test phases
        List<Phase> phases = phaseDomainService.getPhasesByProjectId(tempProjectId);
        assertEquals(1, phases.size());

        assertEquals("BACKLOG", phases.get(0).getName());
        assertEquals(phases.get(0).getId(), phaseCreatedEvent.getPhaseId());
        assertTrue(phases.get(0).isFirst());
        assertTrue(phases.get(0).isLast());
        assertNull(phases.get(0).getPreviousPhase());
        assertNull(phases.get(0).getNextPhase());
    }

    @Test
    public void consumeProjectDeletedEventTest() throws Exception {
        eventCatcher.catchEventOfType(PhaseDeletedEvent.class);
        eventPublisher.publishEvent(new ProjectDeletedEvent(buildUpProjectId));

        // test event
        await().until(eventCatcher::hasCaughtEvent);
        PhaseDeletedEvent phaseDeletedEvent = (PhaseDeletedEvent) eventCatcher.getEvent();
        assertEquals(buildUpProjectId, phaseDeletedEvent.getProjectId());

        // test if phase is actually gone
        assertThrows(NoPhaseFoundException.class, () -> phaseDomainService.getPhasesByProjectId(buildUpProjectId));
    }

    @Test
    public void consumeTicketCreatedEvent() {
        UUID ticketId = UUID.randomUUID();
        eventPublisher.publishEvent(new TicketCreatedEvent(ticketId, buildUpProjectId, userId));

        // get phaseId of buildUpProject
        List<Phase> phases = phaseDomainService.getPhasesByProjectId(buildUpProjectId);
        assertEquals(1, phases.size());
        assertEquals("BACKLOG", phases.get(0).getName());
        UUID phaseId = phases.get(0).getId();

        // test instance
        Phase phase = phaseDomainService.getPhaseById(phases.get(0).getId());
        assertEquals(phase, phases.get(0));
        assertEquals(buildUpProjectId, phase.getProjectId());
        assertEquals(1, phase.getTicketCount());
    }

    @Test
    public void consumeTicketDeletedEvent() {
        UUID ticketId = UUID.randomUUID();
        eventPublisher.publishEvent(new TicketCreatedEvent(ticketId, buildUpProjectId, userId));

        // get phaseId of buildUpProject
        List<Phase> phases = phaseDomainService.getPhasesByProjectId(buildUpProjectId);
        assertEquals("BACKLOG", phases.get(0).getName());
        assertEquals(1, phases.size());
        assertEquals(1, phases.get(0).getTicketCount());
        UUID phaseId = phases.get(0).getId();

        eventPublisher.publishEvent(new TicketDeletedEvent(ticketId, buildUpProjectId, phaseId));

        // test instance
        Phase phase = phaseDomainService.getPhaseById(phaseId);
        assertEquals(phase, phases.get(0));
        assertEquals(buildUpProjectId, phase.getProjectId());
        assertEquals(0, phase.getTicketCount());
    }

    @Test
    public void consumeTicketPhaseUpdatedEventTest() throws Exception {
        // get ids of phases
        List<Phase> phases = phaseDomainService.getPhasesByProjectId(buildUpProjectId);
        assertEquals("BACKLOG", phases.get(0).getName());
        assertEquals(1, phases.size());
        UUID backlogId = phases.get(0).getId();
        UUID doneId = restMinion.postPhase(jwt, buildUpProjectId, "DONE", backlogId);

        // mock post ticket
        UUID ticketId = UUID.randomUUID();
        eventPublisher.publishEvent(new TicketCreatedEvent(ticketId, buildUpProjectId, userId));

        assertEquals(1, phaseDomainService.getPhaseById(backlogId).getTicketCount());
        assertEquals(0, phaseDomainService.getPhaseById(doneId).getTicketCount());

        // change position of ticket
        eventPublisher.publishEvent(new TicketPhaseUpdatedEvent(ticketId, buildUpProjectId, backlogId, doneId));

        // test if ticketCount of phases got updated
        assertEquals(0, phaseDomainService.getPhaseById(backlogId).getTicketCount());
        assertEquals(1, phaseDomainService.getPhaseById(doneId).getTicketCount());
    }
}
