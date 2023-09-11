package com.kett.TicketSystem.notification.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.kett.TicketSystem.membership.domain.events.UnacceptedProjectMembershipCreatedEvent;
import com.kett.TicketSystem.notification.application.dto.NotificationPatchDto;
import com.kett.TicketSystem.notification.domain.Notification;
import com.kett.TicketSystem.notification.domain.NotificationDomainService;
import com.kett.TicketSystem.notification.domain.exceptions.NoNotificationFoundException;
import com.kett.TicketSystem.notification.repository.NotificationRepository;
import com.kett.TicketSystem.ticket.domain.events.TicketAssignedEvent;
import com.kett.TicketSystem.ticket.domain.events.TicketUnassignedEvent;
import com.kett.TicketSystem.user.repository.UserRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles({ "test" })
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class NotificationControllerTests {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final NotificationDomainService notificationDomainService;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final RestRequestHelper restMinion;

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

    private UUID ticketId;
    private UUID projectId;
    private UUID membershipId;

    @Autowired
    public NotificationControllerTests(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            ApplicationEventPublisher eventPublisher,
            NotificationDomainService notificationDomainService,
            NotificationRepository notificationRepository,
            UserRepository userRepository) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.eventPublisher = eventPublisher;
        this.notificationDomainService = notificationDomainService;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.restMinion = new RestRequestHelper(mockMvc, objectMapper);
    }

    @BeforeEach
    public void buildUp() throws Exception {
        userName0 = "Peter Greene";
        userEmail0 = "etepetete.greene@gmail.com";
        userPassword0 = "MyGuitarIsMyLife1337";
        userId0 = restMinion.postUser(userName0, userEmail0, userPassword0);
        jwt0 = restMinion.authenticateUser(userEmail0, userPassword0);

        userName1 = "Julia McGonagall";
        userEmail1 = "julia.MG@hogwarts.uk";
        userPassword1 = "Leviosaaaa!__";
        userId1 = restMinion.postUser(userName1, userEmail1, userPassword1);
        jwt1 = restMinion.authenticateUser(userEmail1, userPassword1);

        ticketId = UUID.randomUUID();
        projectId = UUID.randomUUID();
        membershipId = UUID.randomUUID();

        notificationRepository.deleteAll();
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

        ticketId = null;
        projectId = null;
        membershipId = null;

        notificationRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void getNotificationsByQueryTest() throws Exception {
        eventPublisher.publishEvent(new UnacceptedProjectMembershipCreatedEvent(membershipId, userId0, projectId));
        eventPublisher.publishEvent(new TicketAssignedEvent(ticketId, projectId, userId0));
        eventPublisher.publishEvent(new TicketUnassignedEvent(ticketId, projectId, userId0));

        MvcResult getByRecipientIdResult =
                mockMvc.perform(
                                get("/notifications")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .queryParam("recipientId", userId0.toString())
                                        .header("Authorization", jwt0))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$[0].id").exists())
                        .andExpect(jsonPath("$[0].recipientId").value(userId0.toString()))
                        .andExpect(jsonPath("$[1].id").exists())
                        .andExpect(jsonPath("$[1].recipientId").value(userId0.toString()))
                        .andExpect(jsonPath("$[2].id").exists())
                        .andExpect(jsonPath("$[2].recipientId").value(userId0.toString()))
                        .andReturn();

        MvcResult getByEmailResult =
                mockMvc.perform(
                                get("/notifications")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .queryParam("email", userEmail0)
                                        .header("Authorization", jwt0))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$[0].id").exists())
                        .andExpect(jsonPath("$[0].recipientId").value(userId0.toString()))
                        .andExpect(jsonPath("$[1].id").exists())
                        .andExpect(jsonPath("$[1].recipientId").value(userId0.toString()))
                        .andExpect(jsonPath("$[2].id").exists())
                        .andExpect(jsonPath("$[2].recipientId").value(userId0.toString()))
                        .andReturn();

        assertEquals(getByRecipientIdResult.getResponse().getContentAsString(), getByEmailResult.getResponse().getContentAsString());
    }

    @Test
    public void getNotificationByIdTest() throws Exception {
        eventPublisher.publishEvent(new UnacceptedProjectMembershipCreatedEvent(membershipId, userId0, projectId));

        // find out notificationId
        MvcResult getByRecipientIdResult =
                mockMvc.perform(
                                get("/notifications")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .queryParam("recipientId", userId0.toString())
                                        .header("Authorization", jwt0))
                        .andExpect(status().isOk())
                        .andReturn();
        String getByRecipientIdResponse = getByRecipientIdResult.getResponse().getContentAsString();
        UUID notificationId = UUID.fromString(JsonPath.parse(getByRecipientIdResponse).read("$[0].id"));

        MvcResult getByIdResult =
                mockMvc.perform(
                                get("/notifications/" + notificationId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", jwt0))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(notificationId.toString()))
                        .andExpect(jsonPath("$.recipientId").value(userId0.toString()))
                        .andExpect(jsonPath("$.isRead").value(false))
                        .andReturn();
    }

    @Test
    public void getNotificationByWrongIdTest() throws Exception {
        MvcResult getByIdResult =
                mockMvc.perform(
                                get("/notifications/" + UUID.randomUUID())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", jwt0))
                        .andExpect(status().isNotFound())
                        .andReturn();
    }

    @Test
    public void getNotificationOfOtherUserByIdTest() throws Exception {
        eventPublisher.publishEvent(new TicketAssignedEvent(ticketId, projectId, userId1));

        // find out notificationId
        MvcResult getByRecipientIdResult =
                mockMvc.perform(
                                get("/notifications")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .queryParam("recipientId", userId1.toString())
                                        .header("Authorization", jwt1))
                        .andExpect(status().isOk())
                        .andReturn();
        String getByRecipientIdResponse = getByRecipientIdResult.getResponse().getContentAsString();
        UUID notificationId = UUID.fromString(JsonPath.parse(getByRecipientIdResponse).read("$[0].id"));

        MvcResult getByIdResult =
                mockMvc.perform(
                                get("/notifications/" + notificationId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", jwt0))
                        .andExpect(status().isForbidden())
                        .andReturn();
    }

    @Test
    public void patchNotificationTest() throws Exception {
        eventPublisher.publishEvent(new TicketAssignedEvent(ticketId, projectId, userId0));

        // find out notificationId
        MvcResult getByRecipientIdResult =
                mockMvc.perform(
                                get("/notifications")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .queryParam("recipientId", userId0.toString())
                                        .header("Authorization", jwt0))
                        .andExpect(status().isOk())
                        .andReturn();
        String getByRecipientIdResponse = getByRecipientIdResult.getResponse().getContentAsString();
        UUID notificationId = UUID.fromString(JsonPath.parse(getByRecipientIdResponse).read("$[0].id"));

        // test isRead: false -> true (allowed)
        NotificationPatchDto notificationPatchDto = new NotificationPatchDto(true);
        MvcResult patchResult =
                mockMvc.perform(
                        patch("/notifications/" + notificationId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(notificationPatchDto))
                                .header("Authorization", jwt0))
                        .andExpect(status().isNoContent())
                        .andReturn();
        Notification patchedNotification = notificationDomainService.getNotificationById(notificationId);
        assertEquals(notificationId, patchedNotification.getId());
        assertEquals(userId0, patchedNotification.getRecipientId());
        assertTrue(patchedNotification.getIsRead());

        // test isRead: true -> false (not allowed)
        NotificationPatchDto conflictingNotificationPatchDto = new NotificationPatchDto(false);
        MvcResult conflictingPatchResult =
                mockMvc.perform(
                                patch("/notifications/" + notificationId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(conflictingNotificationPatchDto))
                                        .header("Authorization", jwt0))
                        .andExpect(status().isConflict())
                        .andReturn();
        Notification unpatchedNotification = notificationDomainService.getNotificationById(notificationId);
        assertEquals(patchedNotification, unpatchedNotification);
    }

    @Test
    public void deleteNotificationTest() throws Exception {
        eventPublisher.publishEvent(new TicketAssignedEvent(ticketId, projectId, userId0));

        // find out notificationId
        MvcResult getByRecipientIdResult =
                mockMvc.perform(
                                get("/notifications")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .queryParam("recipientId", userId0.toString())
                                        .header("Authorization", jwt0))
                        .andExpect(status().isOk())
                        .andReturn();
        String getByRecipientIdResponse = getByRecipientIdResult.getResponse().getContentAsString();
        UUID notificationId = UUID.fromString(JsonPath.parse(getByRecipientIdResponse).read("$[0].id"));

        // test delete
        MvcResult deleteResult =
                mockMvc.perform(
                                delete("/notifications/" + notificationId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", jwt0))
                        .andExpect(status().isNoContent())
                        .andReturn();
        assertThrows(NoNotificationFoundException.class, () -> notificationDomainService.getNotificationById(notificationId));
    }

    @Test
    public void deleteNonExistingNotification() throws Exception {
        MvcResult deleteResult =
                mockMvc.perform(
                                delete("/notifications/" + UUID.randomUUID())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", jwt0))
                        .andExpect(status().isNotFound())
                        .andReturn();
    }

    @Test
    public void consumeHandleTicketAssignedEvent() throws Exception {
        eventPublisher
                .publishEvent(
                        new TicketAssignedEvent(
                                ticketId,
                                projectId,
                                userId0
                        )
                );

        // TODO: find more stable alternative for testing
        // shame: give services time to handle event
        Thread.sleep(100);

        List<Notification> notifications = notificationDomainService.getNotificationsByRecipientId(userId0);
        assertEquals(1, notifications.size());
        Notification notification = notifications.get(0);
        assertEquals(userId0, notification.getRecipientId());
        assertEquals(false, notification.getIsRead());
        assertTrue(notification.getContent().contains(ticketId.toString()));
    }

    @Test
    public void consumeUnacceptedProjectMembershipCreatedEvent() throws Exception {
        eventPublisher
                .publishEvent(
                        new UnacceptedProjectMembershipCreatedEvent(
                                membershipId,
                                userId0,
                                projectId
                        )
                );

        // TODO: find more stable alternative for testing
        // shame: give services time to handle event
        Thread.sleep(100);

        List<Notification> notifications = notificationDomainService.getNotificationsByRecipientId(userId0);
        assertEquals(1, notifications.size());
        Notification notification = notifications.get(0);
        assertEquals(userId0, notification.getRecipientId());
        assertEquals(false, notification.getIsRead());
        assertTrue(notification.getContent().contains(projectId.toString()));
    }

    @Test
    public void consumeTicketUnassignedEvent() throws Exception {
        eventPublisher
                .publishEvent(
                        new TicketUnassignedEvent(
                                ticketId,
                                projectId,
                                userId0
                        )
                );

        // TODO: find more stable alternative for testing
        // shame: give services time to handle event
        Thread.sleep(100);

        List<Notification> notifications = notificationDomainService.getNotificationsByRecipientId(userId0);
        assertEquals(1, notifications.size());
        Notification notification = notifications.get(0);
        assertEquals(userId0, notification.getRecipientId());
        assertEquals(false, notification.getIsRead());
        assertTrue(notification.getContent().contains(ticketId.toString()));
    }
}
