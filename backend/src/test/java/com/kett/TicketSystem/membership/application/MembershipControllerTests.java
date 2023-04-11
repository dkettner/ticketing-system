package com.kett.TicketSystem.membership.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.kett.TicketSystem.common.exceptions.ImpossibleException;
import com.kett.TicketSystem.membership.application.dto.MembershipPostDto;
import com.kett.TicketSystem.membership.domain.Membership;
import com.kett.TicketSystem.membership.domain.Role;
import com.kett.TicketSystem.membership.domain.State;
import com.kett.TicketSystem.membership.domain.events.MembershipAcceptedEvent;
import com.kett.TicketSystem.membership.domain.events.MembershipDeletedEvent;
import com.kett.TicketSystem.membership.domain.events.UnacceptedProjectMembershipCreatedEvent;
import com.kett.TicketSystem.membership.domain.exceptions.NoMembershipFoundException;
import com.kett.TicketSystem.membership.repository.MembershipRepository;
import com.kett.TicketSystem.project.domain.events.DefaultProjectCreatedEvent;
import com.kett.TicketSystem.project.domain.events.ProjectCreatedEvent;
import com.kett.TicketSystem.project.domain.events.ProjectDeletedEvent;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class MembershipControllerTests {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final DummyEventListener dummyEventListener;
    private final ApplicationEventPublisher eventPublisher;
    private final RestRequestHelper restMinion;
    private final UserRepository userRepository;
    private final MembershipService membershipService;
    private final MembershipRepository membershipRepository;

    private UUID userId0;
    private String userName0;
    private String userEmail0;
    private String userPassword0;
    private String jwt0;

    private UUID userId1;
    private String userName1;
    private String userEmail1;
    private String userPassword1;
    private String jwt1;

    private UUID projectId;

    @Autowired
    public MembershipControllerTests(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            DummyEventListener dummyEventListener,
            ApplicationEventPublisher eventPublisher,
            UserRepository userRepository,
            MembershipService membershipService,
            MembershipRepository membershipRepository
    ) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.dummyEventListener = dummyEventListener;
        this.eventPublisher = eventPublisher;
        this.restMinion = new RestRequestHelper(mockMvc, objectMapper);
        this.userRepository = userRepository;
        this.membershipService = membershipService;
        this.membershipRepository = membershipRepository;
    }

    @BeforeEach
    public void buildUp() throws Exception {
        userName0 = "Harry Potter";
        userEmail0 = "harry.potter@hw.uk";
        userPassword0 = "snapeShape88";
        userId0 = restMinion.postUser(userName0, userEmail0, userPassword0);
        jwt0 = restMinion.authenticateUser(userEmail0, userPassword0);

        userName1 = "Ronald Weasley";
        userEmail1 = "RonRonRonWeasley@hw.uk";
        userPassword1 = "lkasjdfoijwaefo8238298";
        userId1 = restMinion.postUser(userName1, userEmail1, userPassword1);
        jwt1 = restMinion.authenticateUser(userEmail1, userPassword1);

        projectId = UUID.randomUUID();

        dummyEventListener.deleteAllEvents();
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

        projectId = null;

        membershipRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void postMembershipTest() throws Exception {
        String projectName0 = "Project 0";
        String projectDescription0 = "Description 0";
        UUID projectId0 = restMinion.postProject(jwt0, projectName0, projectDescription0);
        Thread.sleep(100);

        MembershipPostDto membershipPostDto = new MembershipPostDto(projectId0, userId1, Role.MEMBER);
        MvcResult postResult =
                mockMvc.perform(
                                post("/memberships")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(membershipPostDto))
                                        .cookie(new Cookie("jwt", jwt0)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.projectId").value(membershipPostDto.getProjectId().toString()))
                        .andExpect(jsonPath("$.userId").value(membershipPostDto.getUserId().toString()))
                        .andExpect(jsonPath("$.role").value(membershipPostDto.getRole().toString()))
                        .andExpect(jsonPath("$.state").value(State.OPEN.toString()))
                        .andReturn();
        Thread.sleep(100);

        String postResponse = postResult.getResponse().getContentAsString();
        UUID membershipId = UUID.fromString(JsonPath.parse(postResponse).read("$.id"));

        // test UnacceptedProjectMembershipEvent
        Optional<UnacceptedProjectMembershipCreatedEvent> event = dummyEventListener.getLatestUnacceptedProjectMembershipCreatedEvent();
        assertTrue(event.isPresent());
        Optional<UnacceptedProjectMembershipCreatedEvent> emptyEvent = dummyEventListener.getLatestUnacceptedProjectMembershipCreatedEvent();
        assertTrue(emptyEvent.isEmpty()); // check if only one event was thrown
        UnacceptedProjectMembershipCreatedEvent unacceptedProjectMembershipCreatedEvent = event.get();
        assertEquals(membershipId, unacceptedProjectMembershipCreatedEvent.getMembershipId());
        assertEquals(membershipPostDto.getProjectId(), unacceptedProjectMembershipCreatedEvent.getProjectId());
        assertEquals(membershipPostDto.getUserId(), unacceptedProjectMembershipCreatedEvent.getInviteeId());

        // test instance
        Membership membership = membershipService.getMembershipById(membershipId);
        assertEquals(membershipId, membership.getId());
        assertEquals(membershipPostDto.getUserId(), membership.getUserId());
        assertEquals(membershipPostDto.getProjectId(), membership.getProjectId());
        assertEquals(membershipPostDto.getRole(), membership.getRole());
        assertEquals(State.OPEN, membership.getState());
    }

    @Test
    public void deleteOtherMembershipAsAdminTest() throws Exception {
        String projectName0 = "Project 0";
        String projectDescription0 = "Description 0";
        UUID projectId0 = restMinion.postProject(jwt0, projectName0, projectDescription0);
        Thread.sleep(100);

        UUID membershipId = restMinion.postMembership(jwt0, projectId0, userId1, Role.MEMBER);
        Thread.sleep(100);

        MvcResult deleteResult =
                mockMvc.perform(
                                delete("/memberships/" + membershipId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .cookie(new Cookie("jwt", jwt0)))
                        .andExpect(status().isNoContent())
                        .andReturn();

        // test event
        Optional<MembershipDeletedEvent> event = dummyEventListener.getLatestMembershipDeletedEvent();
        assertTrue(event.isPresent());
        Optional<MembershipDeletedEvent> emptyEvent = dummyEventListener.getLatestMembershipDeletedEvent();
        assertTrue(emptyEvent.isEmpty()); // check if only one event was thrown
        MembershipDeletedEvent membershipDeletedEvent = event.get();
        assertEquals(membershipId, membershipDeletedEvent.getMembershipId());
        assertEquals(projectId0, membershipDeletedEvent.getProjectId());
        assertEquals(userId1, membershipDeletedEvent.getUserId());

        // test instance
        assertThrows(NoMembershipFoundException.class, () -> membershipService.getMembershipById(membershipId));
    }

    @Test
    public void deleteOwnMembershipAsMemberTest() throws Exception {
        String projectName0 = "Project 0";
        String projectDescription0 = "Description 0";
        UUID projectId0 = restMinion.postProject(jwt0, projectName0, projectDescription0);
        Thread.sleep(100);

        UUID membershipId = restMinion.postMembership(jwt0, projectId0, userId1, Role.MEMBER);
        Thread.sleep(2000);

        MvcResult deleteResult =
                mockMvc.perform(
                                delete("/memberships/" + membershipId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .cookie(new Cookie("jwt", jwt1)))
                        .andExpect(status().isNoContent())
                        .andReturn();

        // test event
        Optional<MembershipDeletedEvent> event = dummyEventListener.getLatestMembershipDeletedEvent();
        assertTrue(event.isPresent());
        Optional<MembershipDeletedEvent> emptyEvent = dummyEventListener.getLatestMembershipDeletedEvent();
        assertTrue(emptyEvent.isEmpty()); // check if only one event was thrown
        MembershipDeletedEvent membershipDeletedEvent = event.get();
        assertEquals(membershipId, membershipDeletedEvent.getMembershipId());
        assertEquals(projectId0, membershipDeletedEvent.getProjectId());
        assertEquals(userId1, membershipDeletedEvent.getUserId());

        // test instance
        assertThrows(NoMembershipFoundException.class, () -> membershipService.getMembershipById(membershipId));
    }

    @Test
    public void consumeProjectCreatedEventTest() throws Exception {
        eventPublisher.publishEvent(new ProjectCreatedEvent(projectId, userId0));
        Thread.sleep(100);

        // test MembershipAcceptedEvent
        Optional<MembershipAcceptedEvent> event0 = dummyEventListener.getLatestMembershipAcceptedEvent();
        assertTrue(event0.isPresent());
        Optional<MembershipAcceptedEvent> emptyEvent0 = dummyEventListener.getLatestMembershipAcceptedEvent();
        assertTrue(emptyEvent0.isEmpty()); // check if only one event was thrown
        MembershipAcceptedEvent membershipAcceptedEvent = event0.get();
        assertEquals(userId0, membershipAcceptedEvent.getUserId());
        assertEquals(projectId, membershipAcceptedEvent.getProjectId());

        // test membership instance
        UUID membershipId = membershipAcceptedEvent.getMembershipId();
        Membership membership = membershipService.getMembershipById(membershipId);
        assertEquals(membershipId, membership.getId());
        assertEquals(userId0, membership.getUserId());
        assertEquals(projectId, membership.getProjectId());
        assertEquals(Role.ADMIN, membership.getRole());
        assertEquals(State.ACCEPTED, membership.getState());
    }

    @Test
    public void consumeDefaultProjectCreatedEventTest() throws Exception{
        eventPublisher.publishEvent(new DefaultProjectCreatedEvent(projectId, userId0));
        Thread.sleep(100);

        // test MembershipAcceptedEvent
        Optional<MembershipAcceptedEvent> event0 = dummyEventListener.getLatestMembershipAcceptedEvent();
        assertTrue(event0.isPresent());
        Optional<MembershipAcceptedEvent> emptyEvent0 = dummyEventListener.getLatestMembershipAcceptedEvent();
        assertTrue(emptyEvent0.isEmpty()); // check if only one event was thrown
        MembershipAcceptedEvent membershipAcceptedEvent = event0.get();
        assertEquals(userId0, membershipAcceptedEvent.getUserId());
        assertEquals(projectId, membershipAcceptedEvent.getProjectId());

        // test membership instance
        UUID membershipId = membershipAcceptedEvent.getMembershipId();
        Membership membership = membershipService.getMembershipById(membershipId);
        assertEquals(membershipId, membership.getId());
        assertEquals(userId0, membership.getUserId());
        assertEquals(projectId, membership.getProjectId());
        assertEquals(Role.ADMIN, membership.getRole());
        assertEquals(State.ACCEPTED, membership.getState());
    }

    @Test
    public void consumeProjectDeletedEventTest() throws Exception {
        eventPublisher.publishEvent(new ProjectCreatedEvent(projectId, userId0));
        Thread.sleep(100);

        UUID membershipId =
                dummyEventListener
                        .getLatestMembershipAcceptedEvent()
                        .orElseThrow(() -> new ImpossibleException("could not find MembershipAcceptedEvent"))
                        .getMembershipId();

        eventPublisher.publishEvent(new ProjectDeletedEvent(projectId));
        Thread.sleep(100);

        assertThrows(NoMembershipFoundException.class, () -> membershipService.getMembershipById(membershipId));
    }

    @Test
    public void consumeUserDeletedEventTest() throws Exception {
        String projectName0 = "Project 0";
        String projectDescription0 = "Description 0";
        UUID projectId0 = restMinion.postProject(jwt0, projectName0, projectDescription0);
        Thread.sleep(100);

        String projectName1 = "Project 1";
        String projectDescription1 = "Description 1";
        UUID projectId1 = restMinion.postProject(jwt0, projectName1, projectDescription1);
        Thread.sleep(100);

        String projectName2 = "Project 2";
        String projectDescription2 = "Description 2";
        UUID projectId2 = restMinion.postProject(jwt0, projectName2, projectDescription2);
        Thread.sleep(100);

        restMinion.deleteUser(jwt0, userId0);
        Thread.sleep(100);

        assertThrows(NoMembershipFoundException.class, () -> membershipService.getMembershipsByUserId(userId0));
    }
}

