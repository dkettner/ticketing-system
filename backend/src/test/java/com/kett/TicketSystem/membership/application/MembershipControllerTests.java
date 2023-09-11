package com.kett.TicketSystem.membership.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.kett.TicketSystem.membership.application.dto.MembershipPostDto;
import com.kett.TicketSystem.membership.application.dto.MembershipPutRoleDto;
import com.kett.TicketSystem.membership.application.dto.MembershipPutStateDto;
import com.kett.TicketSystem.membership.domain.Membership;
import com.kett.TicketSystem.membership.domain.MembershipDomainService;
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
public class MembershipControllerTests {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final EventCatcher eventCatcher;
    private final ApplicationEventPublisher eventPublisher;
    private final RestRequestHelper restMinion;
    private final UserRepository userRepository;
    private final MembershipDomainService membershipDomainService;
    private final MembershipRepository membershipRepository;

    private UUID userId0;
    private String userName0;
    private String userEmail0;
    private String userPassword0;
    private String jwt0;
    private UUID defaultProjectId0;

    private UUID userId1;
    private String userName1;
    private String userEmail1;
    private String userPassword1;
    private String jwt1;
    private UUID defaultProjectId1;

    private UUID randomProjectId;

    @Autowired
    public MembershipControllerTests(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            EventCatcher eventCatcher,
            ApplicationEventPublisher eventPublisher,
            UserRepository userRepository,
            MembershipDomainService membershipDomainService,
            MembershipRepository membershipRepository
    ) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.eventCatcher = eventCatcher;
        this.eventPublisher = eventPublisher;
        this.restMinion = new RestRequestHelper(mockMvc, objectMapper);
        this.userRepository = userRepository;
        this.membershipDomainService = membershipDomainService;
        this.membershipRepository = membershipRepository;
    }

    @BeforeEach
    public void buildUp() throws Exception {
        userName0 = "Harry Potter";
        userEmail0 = "harry.potter@hw.uk";
        userPassword0 = "snapeShape88";

        eventCatcher.catchEventOfType(DefaultProjectCreatedEvent.class);
        userId0 = restMinion.postUser(userName0, userEmail0, userPassword0);
        await().until(eventCatcher::hasCaughtEvent);
        defaultProjectId0 = ((DefaultProjectCreatedEvent) eventCatcher.getEvent()).getProjectId();

        jwt0 = restMinion.authenticateUser(userEmail0, userPassword0);

        userName1 = "Ronald Weasley";
        userEmail1 = "RonRonRonWeasley@hw.uk";
        userPassword1 = "lkasjdfoijwaefo8238298";

        eventCatcher.catchEventOfType(DefaultProjectCreatedEvent.class);
        userId1 = restMinion.postUser(userName1, userEmail1, userPassword1);
        await().until(eventCatcher::hasCaughtEvent);
        defaultProjectId1 = ((DefaultProjectCreatedEvent) eventCatcher.getEvent()).getProjectId();

        jwt1 = restMinion.authenticateUser(userEmail1, userPassword1);

        randomProjectId = UUID.randomUUID();

        //dummyEventListener.deleteAllEvents();
    }

    @AfterEach
    public void tearDown() {
        userName0 = null;
        userEmail0 = null;
        userPassword0 = null;
        userId0 = null;
        jwt0 = null;
        defaultProjectId0 = null;

        userName1 = null;
        userEmail1 = null;
        userPassword1 = null;
        userId1 = null;
        jwt1 = null;
        defaultProjectId1 = null;

        randomProjectId = null;

        membershipRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void getMembershipByIdTest() throws Exception {
        String projectName0 = "Project 0";
        String projectDescription0 = "Description 0";
        UUID projectId0 = restMinion.postProject(jwt0, projectName0, projectDescription0);

        UUID membershipId = restMinion.postMembership(jwt0, projectId0, userId1, Role.MEMBER);

        MvcResult getResult =
                mockMvc.perform(
                                get("/memberships/" + membershipId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", jwt1))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(membershipId.toString()))
                        .andExpect(jsonPath("$.projectId").value(projectId0.toString()))
                        .andExpect(jsonPath("$.userId").value(userId1.toString()))
                        .andExpect(jsonPath("$.role").value(Role.MEMBER.toString()))
                        .andExpect(jsonPath("$.state").value(State.OPEN.toString()))
                        .andReturn();
    }

    @Test
    public void getMembershipByIdAsAdminTest() throws Exception {
        String projectName0 = "Project 0";
        String projectDescription0 = "Description 0";
        UUID projectId0 = restMinion.postProject(jwt0, projectName0, projectDescription0);

        UUID membershipId = restMinion.postMembership(jwt0, projectId0, userId1, Role.MEMBER);

        MvcResult getResult =
                mockMvc.perform(
                                get("/memberships/" + membershipId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", jwt0))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(membershipId.toString()))
                        .andExpect(jsonPath("$.projectId").value(projectId0.toString()))
                        .andExpect(jsonPath("$.userId").value(userId1.toString()))
                        .andExpect(jsonPath("$.role").value(Role.MEMBER.toString()))
                        .andExpect(jsonPath("$.state").value(State.OPEN.toString()))
                        .andReturn();
    }

    @Test
    public void getMembershipsByUserIdAndEmailQueryTest() throws Exception {
        String projectName0 = "Project 0";
        String projectDescription0 = "Description 0";
        UUID projectId0 = restMinion.postProject(jwt0, projectName0, projectDescription0);

        MvcResult getByUserIdResult =
                mockMvc.perform(
                                get("/memberships")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .queryParam("user-id", userId0.toString())
                                        .header("Authorization", jwt0))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$[0].id").exists())
                        .andExpect(jsonPath("$[0].userId").value(userId0.toString()))
                        .andExpect(jsonPath("$[0].projectId").value(defaultProjectId0.toString()))
                        .andExpect(jsonPath("$[0].role").value(Role.ADMIN.toString()))
                        .andExpect(jsonPath("$[0].state").value(State.ACCEPTED.toString()))
                        .andExpect(jsonPath("$[1].id").exists())
                        .andExpect(jsonPath("$[1].projectId").value(projectId0.toString()))
                        .andExpect(jsonPath("$[1].userId").value(userId0.toString()))
                        .andExpect(jsonPath("$[1].role").value(Role.ADMIN.toString()))
                        .andExpect(jsonPath("$[1].state").value(State.ACCEPTED.toString()))
                        .andReturn();
        MvcResult getByEmailResult =
                mockMvc.perform(
                                get("/memberships")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .queryParam("email", userEmail0)
                                        .header("Authorization", jwt0))
                        .andExpect(status().isOk())
                        .andReturn();
        assertEquals(getByUserIdResult.getResponse().getContentAsString(), getByEmailResult.getResponse().getContentAsString());
    }

    @Test
    public void getMembershipsByProjectIdQueryTest() throws Exception {
        String projectName0 = "Project 0";
        String projectDescription0 = "Description 0";
        UUID projectId0 = restMinion.postProject(jwt0, projectName0, projectDescription0);

        UUID membershipId = restMinion.postMembership(jwt0, projectId0, userId1, Role.MEMBER);

        MvcResult getResult =
                mockMvc.perform(
                                get("/memberships")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .queryParam("project-id", projectId0.toString())
                                        .header("Authorization", jwt0))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$[0].id").exists())
                        .andExpect(jsonPath("$[0].userId").value(userId0.toString()))
                        .andExpect(jsonPath("$[0].projectId").value(projectId0.toString()))
                        .andExpect(jsonPath("$[0].role").value(Role.ADMIN.toString()))
                        .andExpect(jsonPath("$[0].state").value(State.ACCEPTED.toString()))
                        .andExpect(jsonPath("$[1].id").value(membershipId.toString()))
                        .andExpect(jsonPath("$[1].projectId").value(projectId0.toString()))
                        .andExpect(jsonPath("$[1].userId").value(userId1.toString()))
                        .andExpect(jsonPath("$[1].role").value(Role.MEMBER.toString()))
                        .andExpect(jsonPath("$[1].state").value(State.OPEN.toString()))
                        .andReturn();
    }

    @Test
    public void postMembershipTest() throws Exception {
        String projectName0 = "Project 0";
        String projectDescription0 = "Description 0";
        UUID projectId0 = restMinion.postProject(jwt0, projectName0, projectDescription0);

        eventCatcher.catchEventOfType(UnacceptedProjectMembershipCreatedEvent.class);
        MembershipPostDto membershipPostDto = new MembershipPostDto(projectId0, userId1, Role.MEMBER);
        MvcResult postResult =
                mockMvc.perform(
                                post("/memberships")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(membershipPostDto))
                                        .header("Authorization", jwt0))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.projectId").value(membershipPostDto.getProjectId().toString()))
                        .andExpect(jsonPath("$.userId").value(membershipPostDto.getUserId().toString()))
                        .andExpect(jsonPath("$.role").value(membershipPostDto.getRole().toString()))
                        .andExpect(jsonPath("$.state").value(State.OPEN.toString()))
                        .andReturn();
        await().until(eventCatcher::hasCaughtEvent);
        UnacceptedProjectMembershipCreatedEvent unacceptedProjectMembershipCreatedEvent =
                (UnacceptedProjectMembershipCreatedEvent) eventCatcher.getEvent();

        String postResponse = postResult.getResponse().getContentAsString();
        UUID membershipId = UUID.fromString(JsonPath.parse(postResponse).read("$.id"));

        // test event
        assertEquals(membershipId, unacceptedProjectMembershipCreatedEvent.getMembershipId());
        assertEquals(membershipPostDto.getProjectId(), unacceptedProjectMembershipCreatedEvent.getProjectId());
        assertEquals(membershipPostDto.getUserId(), unacceptedProjectMembershipCreatedEvent.getInviteeId());

        // test instance
        Membership membership = membershipDomainService.getMembershipById(membershipId);
        assertEquals(membershipId, membership.getId());
        assertEquals(membershipPostDto.getUserId(), membership.getUserId());
        assertEquals(membershipPostDto.getProjectId(), membership.getProjectId());
        assertEquals(membershipPostDto.getRole(), membership.getRole());
        assertEquals(State.OPEN, membership.getState());
    }

    @Test
    public void putMembershipStateTest() throws Exception {
        String projectName0 = "Project 0";
        String projectDescription0 = "Description 0";
        UUID projectId0 = restMinion.postProject(jwt0, projectName0, projectDescription0);

        UUID membershipId = restMinion.postMembership(jwt0, projectId0, userId1, Role.MEMBER);

        eventCatcher.catchEventOfType(MembershipAcceptedEvent.class);
        MembershipPutStateDto membershipPutStateDto = new MembershipPutStateDto(State.ACCEPTED);
        MvcResult putResult =
                mockMvc.perform(
                                put("/memberships/" + membershipId + "/state")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(membershipPutStateDto))
                                        .header("Authorization", jwt1))
                        .andExpect(status().isNoContent())
                        .andReturn();

        // test event
        await().until(eventCatcher::hasCaughtEvent);
        MembershipAcceptedEvent membershipAcceptedEvent = (MembershipAcceptedEvent) eventCatcher.getEvent();
        assertEquals(membershipId, membershipAcceptedEvent.getMembershipId());
        assertEquals(userId1, membershipAcceptedEvent.getUserId());
        assertEquals(projectId0, membershipAcceptedEvent.getProjectId());

        // test instance
        Membership membership = membershipDomainService.getMembershipById(membershipId);
        assertEquals(membershipId, membership.getId());
        assertEquals(userId1, membership.getUserId());
        assertEquals(projectId0, membership.getProjectId());
        assertEquals(Role.MEMBER, membership.getRole());
        assertEquals(State.ACCEPTED, membership.getState());
    }

    @Test
    public void putMembershipStateBackToOpenTest() throws Exception {
        String projectName0 = "Project 0";
        String projectDescription0 = "Description 0";
        UUID projectId0 = restMinion.postProject(jwt0, projectName0, projectDescription0);

        UUID membershipId = restMinion.postMembership(jwt0, projectId0, userId1, Role.MEMBER);

        // accept
        MembershipPutStateDto membershipPutStateDto0 = new MembershipPutStateDto(State.ACCEPTED);
        MvcResult putResult0 =
                mockMvc.perform(
                                put("/memberships/" + membershipId + "/state")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(membershipPutStateDto0))
                                        .header("Authorization", jwt1))
                        .andExpect(status().isNoContent())
                        .andReturn();

        // try to go back to open
        MembershipPutStateDto membershipPutStateDto1 = new MembershipPutStateDto(State.OPEN);
        MvcResult putResult1 =
                mockMvc.perform(
                                put("/memberships/" + membershipId + "/state")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(membershipPutStateDto1))
                                        .header("Authorization", jwt1))
                        .andExpect(status().isConflict())
                        .andReturn();

        // test instance
        Membership membership = membershipDomainService.getMembershipById(membershipId);
        assertEquals(membershipId, membership.getId());
        assertEquals(userId1, membership.getUserId());
        assertEquals(projectId0, membership.getProjectId());
        assertEquals(Role.MEMBER, membership.getRole());
        assertEquals(State.ACCEPTED, membership.getState());
    }

    @Test
    public void putMembershipStateBackToOpenAsAdminTest() throws Exception {
        String projectName0 = "Project 0";
        String projectDescription0 = "Description 0";
        UUID projectId0 = restMinion.postProject(jwt0, projectName0, projectDescription0);
        UUID membershipId = restMinion.postMembership(jwt0, projectId0, userId1, Role.MEMBER);

        // accept
        MembershipPutStateDto membershipPutStateDto0 = new MembershipPutStateDto(State.ACCEPTED);
        MvcResult putResult0 =
                mockMvc.perform(
                                put("/memberships/" + membershipId + "/state")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(membershipPutStateDto0))
                                        .header("Authorization", jwt1))
                        .andExpect(status().isNoContent())
                        .andReturn();

        // try to go back to open
        MembershipPutStateDto membershipPutStateDto1 = new MembershipPutStateDto(State.OPEN);
        MvcResult putResult1 =
                mockMvc.perform(
                                put("/memberships/" + membershipId + "/state")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(membershipPutStateDto1))
                                        .header("Authorization", jwt0))
                        .andExpect(status().isForbidden())
                        .andReturn();

        // test instance
        Membership membership = membershipDomainService.getMembershipById(membershipId);
        assertEquals(membershipId, membership.getId());
        assertEquals(userId1, membership.getUserId());
        assertEquals(projectId0, membership.getProjectId());
        assertEquals(Role.MEMBER, membership.getRole());
        assertEquals(State.ACCEPTED, membership.getState());
    }

    @Test
    public void putMembershipStateAsAdminTest() throws Exception {
        String projectName0 = "Project 0";
        String projectDescription0 = "Description 0";
        UUID projectId0 = restMinion.postProject(jwt0, projectName0, projectDescription0);
        UUID membershipId = restMinion.postMembership(jwt0, projectId0, userId1, Role.MEMBER);

        MembershipPutStateDto membershipPutStateDto = new MembershipPutStateDto(State.ACCEPTED);
        MvcResult putResult =
                mockMvc.perform(
                                put("/memberships/" + membershipId + "/state")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(membershipPutStateDto))
                                        .header("Authorization", jwt0))
                        .andExpect(status().isForbidden())
                        .andReturn();

        // test instance
        Membership membership = membershipDomainService.getMembershipById(membershipId);
        assertEquals(membershipId, membership.getId());
        assertEquals(userId1, membership.getUserId());
        assertEquals(projectId0, membership.getProjectId());
        assertEquals(Role.MEMBER, membership.getRole());
        assertEquals(State.OPEN, membership.getState());
    }

    @Test
    public void putMembershipRoleAsAdminTest() throws Exception {
        String projectName0 = "Project 0";
        String projectDescription0 = "Description 0";
        UUID projectId0 = restMinion.postProject(jwt0, projectName0, projectDescription0);
        UUID membershipId = restMinion.postMembership(jwt0, projectId0, userId1, Role.MEMBER);

        MembershipPutRoleDto membershipPutStateDto = new MembershipPutRoleDto(Role.ADMIN);
        MvcResult putResult =
                mockMvc.perform(
                                put("/memberships/" + membershipId + "/role")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(membershipPutStateDto))
                                        .header("Authorization", jwt0))
                        .andExpect(status().isNoContent())
                        .andReturn();

        // test instance
        Membership membership = membershipDomainService.getMembershipById(membershipId);
        assertEquals(membershipId, membership.getId());
        assertEquals(userId1, membership.getUserId());
        assertEquals(projectId0, membership.getProjectId());
        assertEquals(Role.ADMIN, membership.getRole());
        assertEquals(State.OPEN, membership.getState());
    }

    @Test
    public void putMembershipRoleTest() throws Exception {
        String projectName0 = "Project 0";
        String projectDescription0 = "Description 0";
        UUID projectId0 = restMinion.postProject(jwt0, projectName0, projectDescription0);
        UUID membershipId = restMinion.postMembership(jwt0, projectId0, userId1, Role.MEMBER);

        MembershipPutRoleDto membershipPutStateDto = new MembershipPutRoleDto(Role.ADMIN);
        MvcResult putResult =
                mockMvc.perform(
                                put("/memberships/" + membershipId + "/role")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(membershipPutStateDto))
                                        .header("Authorization", jwt1))
                        .andExpect(status().isForbidden())
                        .andReturn();

        // test instance
        Membership membership = membershipDomainService.getMembershipById(membershipId);
        assertEquals(membershipId, membership.getId());
        assertEquals(userId1, membership.getUserId());
        assertEquals(projectId0, membership.getProjectId());
        assertEquals(Role.MEMBER, membership.getRole());
        assertEquals(State.OPEN, membership.getState());
    }

    @Test
    public void deleteOtherMembershipAsAdminTest() throws Exception {
        String projectName0 = "Project 0";
        String projectDescription0 = "Description 0";
        UUID projectId0 = restMinion.postProject(jwt0, projectName0, projectDescription0);
        UUID membershipId = restMinion.postMembership(jwt0, projectId0, userId1, Role.MEMBER);

        eventCatcher.catchEventOfType(MembershipDeletedEvent.class);
        MvcResult deleteResult =
                mockMvc.perform(
                                delete("/memberships/" + membershipId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", jwt0))
                        .andExpect(status().isNoContent())
                        .andReturn();

        // test event
        await().until(eventCatcher::hasCaughtEvent);
        MembershipDeletedEvent membershipDeletedEvent = (MembershipDeletedEvent) eventCatcher.getEvent();
        assertEquals(membershipId, membershipDeletedEvent.getMembershipId());
        assertEquals(projectId0, membershipDeletedEvent.getProjectId());
        assertEquals(userId1, membershipDeletedEvent.getUserId());

        // test instance
        assertThrows(NoMembershipFoundException.class, () -> membershipDomainService.getMembershipById(membershipId));
    }

    @Test
    public void deleteOwnMembershipAsMemberTest() throws Exception {
        String projectName0 = "Project 0";
        String projectDescription0 = "Description 0";
        UUID projectId0 = restMinion.postProject(jwt0, projectName0, projectDescription0);
        UUID membershipId = restMinion.postMembership(jwt0, projectId0, userId1, Role.MEMBER);

        eventCatcher.catchEventOfType(MembershipDeletedEvent.class);
        MvcResult deleteResult =
                mockMvc.perform(
                                delete("/memberships/" + membershipId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", jwt1))
                        .andExpect(status().isNoContent())
                        .andReturn();

        // test event
        await().until(eventCatcher::hasCaughtEvent);
        MembershipDeletedEvent membershipDeletedEvent = (MembershipDeletedEvent) eventCatcher.getEvent();
        assertEquals(membershipId, membershipDeletedEvent.getMembershipId());
        assertEquals(projectId0, membershipDeletedEvent.getProjectId());
        assertEquals(userId1, membershipDeletedEvent.getUserId());

        // test instance
        assertThrows(NoMembershipFoundException.class, () -> membershipDomainService.getMembershipById(membershipId));
    }

    @Test
    public void consumeProjectCreatedEventTest() {
        eventCatcher.catchEventOfType(MembershipAcceptedEvent.class);
        eventPublisher.publishEvent(new ProjectCreatedEvent(randomProjectId, userId0));

        // test MembershipAcceptedEvent
        await().until(eventCatcher::hasCaughtEvent);
        MembershipAcceptedEvent membershipAcceptedEvent = (MembershipAcceptedEvent) eventCatcher.getEvent();
        assertEquals(userId0, membershipAcceptedEvent.getUserId());
        assertEquals(randomProjectId, membershipAcceptedEvent.getProjectId());

        // test membership instance
        UUID membershipId = membershipAcceptedEvent.getMembershipId();
        Membership membership = membershipDomainService.getMembershipById(membershipId);
        assertEquals(membershipId, membership.getId());
        assertEquals(userId0, membership.getUserId());
        assertEquals(randomProjectId, membership.getProjectId());
        assertEquals(Role.ADMIN, membership.getRole());
        assertEquals(State.ACCEPTED, membership.getState());
    }

    @Test
    public void consumeDefaultProjectCreatedEventTest() {
        eventCatcher.catchEventOfType(MembershipAcceptedEvent.class);
        eventPublisher.publishEvent(new DefaultProjectCreatedEvent(randomProjectId, userId0));

        // test MembershipAcceptedEvent
        await().until(eventCatcher::hasCaughtEvent);
        MembershipAcceptedEvent membershipAcceptedEvent = (MembershipAcceptedEvent) eventCatcher.getEvent();
        assertEquals(userId0, membershipAcceptedEvent.getUserId());
        assertEquals(randomProjectId, membershipAcceptedEvent.getProjectId());

        // test membership instance
        UUID membershipId = membershipAcceptedEvent.getMembershipId();
        Membership membership = membershipDomainService.getMembershipById(membershipId);
        assertEquals(membershipId, membership.getId());
        assertEquals(userId0, membership.getUserId());
        assertEquals(randomProjectId, membership.getProjectId());
        assertEquals(Role.ADMIN, membership.getRole());
        assertEquals(State.ACCEPTED, membership.getState());
    }

    @Test
    public void consumeProjectDeletedEventTest() {
        eventCatcher.catchEventOfType(MembershipAcceptedEvent.class);
        eventPublisher.publishEvent(new ProjectCreatedEvent(randomProjectId, userId0));

        await().until(eventCatcher::hasCaughtEvent);
        UUID membershipId = ((MembershipAcceptedEvent) eventCatcher.getEvent()).getMembershipId();

        eventCatcher.catchEventOfType(MembershipDeletedEvent.class);
        eventPublisher.publishEvent(new ProjectDeletedEvent(randomProjectId));

        // test event
        await().until(eventCatcher::hasCaughtEvent);
        MembershipDeletedEvent membershipDeletedEvent = (MembershipDeletedEvent) eventCatcher.getEvent();
        assertEquals(membershipId, membershipDeletedEvent.getMembershipId());
        assertEquals(randomProjectId, membershipDeletedEvent.getProjectId());
        assertEquals(userId0, membershipDeletedEvent.getUserId());

        // test instance
        assertThrows(NoMembershipFoundException.class, () -> membershipDomainService.getMembershipById(membershipId));
    }

    @Test
    public void consumeUserDeletedEventTest() throws Exception {
        String projectName0 = "Project 0";
        String projectDescription0 = "Description 0";
        UUID projectId0 = restMinion.postProject(jwt0, projectName0, projectDescription0);

        String projectName1 = "Project 1";
        String projectDescription1 = "Description 1";
        UUID projectId1 = restMinion.postProject(jwt0, projectName1, projectDescription1);

        String projectName2 = "Project 2";
        String projectDescription2 = "Description 2";
        UUID projectId2 = restMinion.postProject(jwt0, projectName2, projectDescription2);

        restMinion.deleteUser(jwt0, userId0);

        assertThrows(NoMembershipFoundException.class, () -> membershipDomainService.getMembershipsByUserId(userId0));
    }
}

