package com.kett.TicketSystem.ticket.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.kett.TicketSystem.membership.domain.Role;
import com.kett.TicketSystem.membership.domain.State;
import com.kett.TicketSystem.membership.repository.MembershipRepository;
import com.kett.TicketSystem.phase.repository.PhaseRepository;
import com.kett.TicketSystem.project.repository.ProjectRepository;
import com.kett.TicketSystem.ticket.application.dto.TicketPatchDto;
import com.kett.TicketSystem.ticket.application.dto.TicketPostDto;
import com.kett.TicketSystem.ticket.domain.Ticket;
import com.kett.TicketSystem.ticket.domain.TicketDomainService;
import com.kett.TicketSystem.ticket.domain.events.TicketAssignedEvent;
import com.kett.TicketSystem.ticket.domain.events.TicketCreatedEvent;
import com.kett.TicketSystem.ticket.domain.events.TicketDeletedEvent;
import com.kett.TicketSystem.ticket.domain.events.TicketPhaseUpdatedEvent;
import com.kett.TicketSystem.ticket.domain.exceptions.NoTicketFoundException;
import com.kett.TicketSystem.ticket.repository.TicketRepository;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
public class TicketControllerTests {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final RestRequestHelper restMinion;
    private final ApplicationEventPublisher eventPublisher;
    private final EventCatcher eventCatcher;
    private final TicketDomainService ticketDomainService;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final MembershipRepository membershipRepository;
    private final PhaseRepository phaseRepository;

    private String userName0;
    private String userEmail0;
    private String userPassword0;
    private UUID userId0;
    private String jwt0;

    private String userName1;
    private String userEmail1;
    private String userPassword1;
    private UUID userId1;
    private String jwt1;

    private String userName2;
    private String userEmail2;
    private String userPassword2;
    private UUID userId2;
    private String jwt2;

    private String buildUpProjectName;
    private String buildUpProjectDescription;
    private UUID buildUpProjectId;

    private String ticketTitle0;
    private String ticketDescription0;
    private LocalDateTime dateOfTomorrow;

    @Autowired
    public TicketControllerTests(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            ApplicationEventPublisher eventPublisher,
            EventCatcher eventCatcher,
            TicketDomainService ticketDomainService,
            TicketRepository ticketRepository,
            UserRepository userRepository,
            ProjectRepository projectRepository,
            MembershipRepository membershipRepository,
            PhaseRepository phaseRepository
    ) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.projectRepository = projectRepository;
        this.restMinion = new RestRequestHelper(mockMvc, objectMapper);
        this.eventPublisher = eventPublisher;
        this.eventCatcher = eventCatcher;
        this.ticketDomainService = ticketDomainService;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
        this.phaseRepository = phaseRepository;
    }

    @BeforeEach
    public void buildUp() throws Exception {
        userName0 = "Geralt";
        userEmail0 = "il.brucho@netflix.com";
        userPassword0 = "DiesDasAnanasiospjefosias9999023";
        userId0 = restMinion.postUser(userName0, userEmail0, userPassword0);
        jwt0 = restMinion.authenticateUser(userEmail0, userPassword0);

        userName1 = "Harry Potter";
        userEmail1 = "harry.potter@hw.uk";
        userPassword1 = "snapeShape88";
        userId1 = restMinion.postUser(userName1, userEmail1, userPassword1);
        jwt1 = restMinion.authenticateUser(userEmail1, userPassword1);

        userName2 = "Ronald Weasley";
        userEmail2 = "RonRonRonWeasley@hw.uk";
        userPassword2 = "lkasjdfoijwaefo8238298";
        userId2 = restMinion.postUser(userName2, userEmail2, userPassword2);
        jwt2 = restMinion.authenticateUser(userEmail2, userPassword2);

        buildUpProjectName = "toss a coin to your witcher";
        buildUpProjectDescription = "50ct please";
        buildUpProjectId = restMinion.postProject(jwt0, buildUpProjectName, buildUpProjectDescription);

        UUID membershipId = restMinion.postMembership(jwt0, buildUpProjectId, userId1, Role.MEMBER);
        restMinion.putMembershipState(jwt1, membershipId, State.ACCEPTED);

        ticketTitle0 = "My first ticket";
        ticketDescription0 = "do stuff";
        dateOfTomorrow = LocalDateTime.now().plusDays(1);
    }

    @AfterEach
    public void tearDown() {
        userName0 = null;
        userEmail0 = null;
        userPassword0 = null;
        userId0 = null;
        jwt0 = null;

        userName1 = null;
        userEmail1 = null;
        userPassword1 = null;
        userId1 = null;
        jwt1 = null;

        userName2 = null;
        userEmail2 = null;
        userPassword2 = null;
        userId2 = null;
        jwt2 = null;

        buildUpProjectName = null;
        buildUpProjectDescription = null;
        buildUpProjectId = null;

        ticketTitle0 = null;
        ticketDescription0 = null;

        ticketRepository.deleteAll();
        userRepository.deleteAll();
        phaseRepository.deleteAll();
        projectRepository.deleteAll();
        membershipRepository.deleteAll();
    }

    @Test
    public void getTicketByIdTest() throws Exception {
        UUID ticketId = restMinion.postTicket(
                jwt0, buildUpProjectId, ticketTitle0, ticketDescription0, dateOfTomorrow, new ArrayList<>()
        );

        MvcResult getResult =
                mockMvc.perform(
                                get("/tickets/" + ticketId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", jwt0))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(ticketId.toString()))
                        .andExpect(jsonPath("$.projectId").value(buildUpProjectId.toString()))
                        .andExpect(jsonPath("$.title").value(ticketTitle0))
                        .andExpect(jsonPath("$.description").value(ticketDescription0))
                        .andExpect(jsonPath("$.phaseId").exists())
                        .andExpect(jsonPath("$.creationTime").exists())
                        .andExpect(jsonPath("$.dueTime").exists())
                        .andReturn();
    }

    @Test
    public void getTicketByPhaseIdQueryTest() throws Exception {
        UUID ticketId0 = restMinion.postTicket(
                jwt0, buildUpProjectId, ticketTitle0, ticketDescription0, dateOfTomorrow, new ArrayList<>()
        );

        String ticketTitle1 = "blub";
        String ticketDescription1 = "asdlkfjaslkdfasdf";
        LocalDateTime dateOfTheDayAfterTomorrow = dateOfTomorrow.plusDays(1);
        UUID ticketId1 = restMinion.postTicket(
                jwt0, buildUpProjectId, ticketTitle1, ticketDescription1, dateOfTheDayAfterTomorrow, new ArrayList<>()
        );

        UUID backlogId = ticketDomainService.getTicketById(ticketId0).getPhaseId();
        MvcResult getResult =
                mockMvc.perform(
                                get("/tickets" )
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .queryParam("phase-id", backlogId.toString())
                                        .header("Authorization", jwt0))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$").isArray())
                        .andExpect(jsonPath("$[0].id").value(ticketId0.toString()))
                        .andExpect(jsonPath("$[0].projectId").value(buildUpProjectId.toString()))
                        .andExpect(jsonPath("$[0].title").value(ticketTitle0))
                        .andExpect(jsonPath("$[0].description").value(ticketDescription0))
                        .andExpect(jsonPath("$[0].phaseId").value(backlogId.toString()))
                        .andExpect(jsonPath("$[0].creationTime").exists())
                        .andExpect(jsonPath("$[0].dueTime").exists())
                        .andExpect(jsonPath("$[1].id").value(ticketId1.toString()))
                        .andExpect(jsonPath("$[1].projectId").value(buildUpProjectId.toString()))
                        .andExpect(jsonPath("$[1].title").value(ticketTitle1))
                        .andExpect(jsonPath("$[1].description").value(ticketDescription1))
                        .andExpect(jsonPath("$[1].phaseId").value(backlogId.toString()))
                        .andExpect(jsonPath("$[1].creationTime").exists())
                        .andExpect(jsonPath("$[1].dueTime").exists())
                        .andReturn();
    }

    @Test
    public void getTicketByProjectIdQueryTest() throws Exception {
        // first ticket
        UUID ticketId0 = restMinion.postTicket(
                jwt0, buildUpProjectId, ticketTitle0, ticketDescription0, dateOfTomorrow, new ArrayList<>()
        );

        // post a second phase
        UUID backlogId = ticketDomainService.getTicketById(ticketId0).getPhaseId();
        UUID donePhaseId = restMinion.postPhase(jwt0, buildUpProjectId, "DONE", backlogId);

        // second ticket
        String ticketTitle1 = "blub";
        String ticketDescription1 = "asdlkfjaslkdfasdf";
        LocalDateTime dateOfTheDayAfterTomorrow = dateOfTomorrow.plusDays(1);
        UUID ticketId1 = restMinion.postTicket(
                jwt0, buildUpProjectId, ticketTitle1, ticketDescription1, dateOfTheDayAfterTomorrow, new ArrayList<>()
        );

        // move first ticket to second phase
        restMinion.patchTicket(jwt0, ticketId0, null, null, null, donePhaseId, null);

        MvcResult getResult =
                mockMvc.perform(
                                get("/tickets" )
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .queryParam("project-id", buildUpProjectId.toString())
                                        .header("Authorization", jwt0))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$").isArray())
                        .andExpect(jsonPath("$[0].id").value(ticketId0.toString()))
                        .andExpect(jsonPath("$[0].projectId").value(buildUpProjectId.toString()))
                        .andExpect(jsonPath("$[0].title").value(ticketTitle0))
                        .andExpect(jsonPath("$[0].description").value(ticketDescription0))
                        .andExpect(jsonPath("$[0].phaseId").value(donePhaseId.toString()))
                        .andExpect(jsonPath("$[0].creationTime").exists())
                        .andExpect(jsonPath("$[0].dueTime").exists())
                        .andExpect(jsonPath("$[1].id").value(ticketId1.toString()))
                        .andExpect(jsonPath("$[1].projectId").value(buildUpProjectId.toString()))
                        .andExpect(jsonPath("$[1].title").value(ticketTitle1))
                        .andExpect(jsonPath("$[1].description").value(ticketDescription1))
                        .andExpect(jsonPath("$[1].phaseId").value(backlogId.toString()))
                        .andExpect(jsonPath("$[1].creationTime").exists())
                        .andExpect(jsonPath("$[1].dueTime").exists())
                        .andReturn();
    }

    @Test
    public void getTicketByAssigneeIdQueryTest() throws Exception {
        List<UUID> assigneeIds = new ArrayList<>();
        assigneeIds.add(userId1);

        // first ticket to first project
        UUID ticketId0 = restMinion.postTicket(
                jwt0, buildUpProjectId, ticketTitle0, ticketDescription0, dateOfTomorrow, assigneeIds
        );

        // post a second project of different user
        String secondTitle = "second title";
        String secondDescription = "second description";
        UUID secondProjectId = restMinion.postProject(jwt1, secondTitle, secondDescription);

        // post second ticket to second project
        String ticketTitle1 = "blub";
        String ticketDescription1 = "asdlkfjaslkdfasdf";
        LocalDateTime dateOfTheDayAfterTomorrow = dateOfTomorrow.plusDays(1);
        UUID ticketId1 = restMinion.postTicket(
                jwt1, secondProjectId, ticketTitle1, ticketDescription1, dateOfTheDayAfterTomorrow, assigneeIds
        );

        MvcResult getResult =
                mockMvc.perform(
                                get("/tickets" )
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .queryParam("assignee-id", userId1.toString())
                                        .header("Authorization", jwt1))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$").isArray())
                        .andExpect(jsonPath("$[0].id").value(ticketId0.toString()))
                        .andExpect(jsonPath("$[0].projectId").value(buildUpProjectId.toString()))
                        .andExpect(jsonPath("$[0].title").value(ticketTitle0))
                        .andExpect(jsonPath("$[0].description").value(ticketDescription0))
                        .andExpect(jsonPath("$[0].phaseId").exists())
                        .andExpect(jsonPath("$[0].creationTime").exists())
                        .andExpect(jsonPath("$[0].dueTime").exists())
                        .andExpect(jsonPath("$[1].id").value(ticketId1.toString()))
                        .andExpect(jsonPath("$[1].projectId").value(secondProjectId.toString()))
                        .andExpect(jsonPath("$[1].title").value(ticketTitle1))
                        .andExpect(jsonPath("$[1].description").value(ticketDescription1))
                        .andExpect(jsonPath("$[1].phaseId").exists())
                        .andExpect(jsonPath("$[1].creationTime").exists())
                        .andExpect(jsonPath("$[1].dueTime").exists())
                        .andReturn();
    }

    @Test
    public void postTicketTest() throws Exception {
        eventCatcher.catchEventOfType(TicketCreatedEvent.class);
        TicketPostDto ticketPostDto = new TicketPostDto(buildUpProjectId, ticketTitle0, ticketDescription0, dateOfTomorrow, new ArrayList<>());
        MvcResult postResult0 =
                mockMvc.perform(
                                post("/tickets")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(ticketPostDto))
                                        .header("Authorization", jwt0))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.projectId").value(buildUpProjectId.toString()))
                        .andExpect(jsonPath("$.title").value(ticketTitle0))
                        .andExpect(jsonPath("$.description").value(ticketDescription0))
                        .andExpect(jsonPath("$.phaseId").exists())
                        .andExpect(jsonPath("$.creationTime").exists())
                        .andExpect(jsonPath("$.dueTime").exists())
                        .andReturn();
        String postResponse0 = postResult0.getResponse().getContentAsString();
        UUID ticketId = UUID.fromString(JsonPath.parse(postResponse0).read("$.id"));

        // test event
        await().until(eventCatcher::hasCaughtEvent);
        TicketCreatedEvent ticketCreatedEvent = (TicketCreatedEvent) eventCatcher.getEvent();
        assertEquals(ticketId, ticketCreatedEvent.getTicketId());
        assertEquals(ticketPostDto.getProjectId(), ticketCreatedEvent.getProjectId());
        assertEquals(userId0, ticketCreatedEvent.getUserId());

        // test instance
        Ticket ticket = ticketDomainService.getTicketById(ticketId);
        assertEquals(ticketId, ticket.getId());
        assertEquals(ticketPostDto.getTitle(), ticket.getTitle());
        assertEquals(ticketPostDto.getDescription(), ticket.getDescription());
        assertEquals(ticketPostDto.getAssigneeIds(), ticket.getAssigneeIds());
        assertEquals(ticketPostDto.getDueTime(), ticket.getDueTime());
        assertTrue(ticket.getCreationTime().isBefore(LocalDateTime.now()));
    }

    @Test
    public void patchTicketNameAndDescriptionAndDueTimeTest() throws Exception {
        UUID ticketId = restMinion.postTicket(
                jwt0, buildUpProjectId, ticketTitle0, ticketDescription0, dateOfTomorrow, new ArrayList<>()
        );

        String newTitle = "a totally new title";
        String newDescription = "never seen anything like this";
        LocalDateTime newDueTime = dateOfTomorrow.plusDays(1);
        TicketPatchDto ticketPatchDto = new TicketPatchDto(newTitle, newDescription, newDueTime, null, null);
        MvcResult patchResult =
                mockMvc.perform(
                                patch("/tickets/" + ticketId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(ticketPatchDto))
                                        .header("Authorization", jwt0))
                        .andExpect(status().isNoContent())
                        .andReturn();

        // test instance
        Ticket ticket = ticketDomainService.getTicketById(ticketId);
        assertEquals(ticketId, ticket.getId());
        assertEquals(buildUpProjectId, ticket.getProjectId());
        assertEquals(ticketPatchDto.getTitle(), ticket.getTitle());
        assertEquals(ticketPatchDto.getDescription(), ticket.getDescription());
        assertEquals(ticketPatchDto.getDueTime(), ticket.getDueTime());
    }

    @Test
    public void patchTicketPhaseIdTimeTest() throws Exception {
        UUID ticketId = restMinion.postTicket(
                jwt0, buildUpProjectId, ticketTitle0, ticketDescription0, dateOfTomorrow, new ArrayList<>()
        );
        UUID backlogPhaseId = ticketDomainService.getTicketById(ticketId).getPhaseId();
        UUID donePhaseId = restMinion.postPhase(jwt0, buildUpProjectId, "DONE", backlogPhaseId);

        eventCatcher.catchEventOfType(TicketPhaseUpdatedEvent.class);
        TicketPatchDto ticketPatchDto = new TicketPatchDto(null, null, null, donePhaseId, null);
        MvcResult patchResult =
                mockMvc.perform(
                                patch("/tickets/" + ticketId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(ticketPatchDto))
                                        .header("Authorization", jwt0))
                        .andExpect(status().isNoContent())
                        .andReturn();

        // test event
        await().until(eventCatcher::hasCaughtEvent);
        TicketPhaseUpdatedEvent ticketPhaseUpdatedEvent = (TicketPhaseUpdatedEvent) eventCatcher.getEvent();
        assertEquals(ticketId, ticketPhaseUpdatedEvent.getTicketId());
        assertEquals(buildUpProjectId, ticketPhaseUpdatedEvent.getProjectId());
        assertEquals(donePhaseId, ticketPhaseUpdatedEvent.getNewPhaseId());
        assertEquals(backlogPhaseId, ticketPhaseUpdatedEvent.getOldPhaseId());

        // test instance
        Ticket ticket = ticketDomainService.getTicketById(ticketId);
        assertEquals(ticketId, ticket.getId());
        assertEquals(buildUpProjectId, ticket.getProjectId());
        assertEquals(ticketPatchDto.getPhaseId(), ticket.getPhaseId());
        assertEquals(ticketTitle0, ticket.getTitle());
        assertEquals(ticketDescription0, ticket.getDescription());
        assertEquals(dateOfTomorrow, ticket.getDueTime());
    }

    @Test
    public void patchTicketAssigneeIdsTest() throws Exception {
        UUID ticketId = restMinion.postTicket(
                jwt0, buildUpProjectId, ticketTitle0, ticketDescription0, dateOfTomorrow, new ArrayList<>()
        );

        eventCatcher.catchEventOfType(TicketAssignedEvent.class);
        List<UUID> assigneeIds = new ArrayList<>();
        assigneeIds.add(userId1);
        TicketPatchDto ticketPatchDto = new TicketPatchDto(null, null, null, null, assigneeIds);
        MvcResult patchResult =
                mockMvc.perform(
                                patch("/tickets/" + ticketId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(ticketPatchDto))
                                        .header("Authorization", jwt0))
                        .andExpect(status().isNoContent())
                        .andReturn();

        // test event
        await().until(eventCatcher::hasCaughtEvent);
        TicketAssignedEvent ticketAssignedEvent = (TicketAssignedEvent) eventCatcher.getEvent();
        assertEquals(ticketId, ticketAssignedEvent.getTicketId());
        assertEquals(buildUpProjectId, ticketAssignedEvent.getProjectId());
        assertEquals(assigneeIds.get(0), ticketAssignedEvent.getAssigneeId());

        // test instance
        Ticket ticket = ticketDomainService.getTicketById(ticketId);
        assertEquals(ticketId, ticket.getId());
        assertEquals(buildUpProjectId, ticket.getProjectId());
        assertTrue(ticket.isAssignee(assigneeIds.get(0)));
        assertEquals(ticketTitle0, ticket.getTitle());
        assertEquals(ticketDescription0, ticket.getDescription());
        assertEquals(dateOfTomorrow, ticket.getDueTime());
    }

    @Test
    public void deleteTicketTest() throws Exception {
        UUID ticketId = restMinion.postTicket(
                jwt0, buildUpProjectId, ticketTitle0, ticketDescription0, dateOfTomorrow, new ArrayList<>()
        );

        eventCatcher.catchEventOfType(TicketDeletedEvent.class);
        MvcResult deleteResult =
                mockMvc.perform(
                                delete("/tickets/" + ticketId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", jwt0))
                        .andExpect(status().isNoContent())
                        .andReturn();

        // test event
        await().until(eventCatcher::hasCaughtEvent);
        TicketDeletedEvent ticketDeletedEvent = (TicketDeletedEvent) eventCatcher.getEvent();
        assertEquals(ticketId, ticketDeletedEvent.getTicketId());
        assertEquals(buildUpProjectId, ticketDeletedEvent.getProjectId());

        // test instance
        assertThrows(NoTicketFoundException.class, () -> ticketDomainService.getTicketById(ticketId));
    }
}
