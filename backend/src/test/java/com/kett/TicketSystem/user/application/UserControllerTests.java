package com.kett.TicketSystem.user.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.kett.TicketSystem.authentication.application.dto.AuthenticationPostDto;
import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import com.kett.TicketSystem.common.exceptions.NoUserFoundException;
import com.kett.TicketSystem.user.application.dto.UserPatchDto;
import com.kett.TicketSystem.user.application.dto.UserPostDto;
import com.kett.TicketSystem.user.domain.User;
import com.kett.TicketSystem.user.domain.UserDomainService;
import com.kett.TicketSystem.user.domain.events.UserCreatedEvent;
import com.kett.TicketSystem.user.domain.events.UserDeletedEvent;
import com.kett.TicketSystem.user.domain.events.UserPatchedEvent;
import com.kett.TicketSystem.user.repository.UserRepository;
import com.kett.TicketSystem.util.EventCatcher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles({ "test" })
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerTests {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final UserDomainService userDomainService;
    private final EventCatcher eventCatcher;

    private String name0;
    private String email0;
    private String password0;

    private String name1;
    private String email1;
    private String password1;

    private String name2;
    private String email2;
    private String password2;

    private String name3;
    private String email3;
    private String password3;

    private String id4;
    private String name4;
    private String email4;
    private String password4;
    private String jwt4;


    @Autowired
    public UserControllerTests(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            UserRepository userRepository,
            UserDomainService userDomainService,
            EventCatcher eventCatcher
    ) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.userDomainService = userDomainService;
        this.eventCatcher = eventCatcher;
    }

    @BeforeEach
    public void buildUp() throws Exception {

        // validUser0
        name0 = "Bill Gates";
        email0 = "dollar.bill@microsoft.com";
        password0 = "hereComesThe_Money1337";

        // validUser1
        name1 = "Jeff Bezos";
        email1 = "say_my_name@amazon.com";
        password1 = "ioiisdfoipsd0203kf0k";

        // invalidUser0
        name2 = "Elon Mosquito";
        email2 = "say_my_nameamazon.com"; // missing @
        password2 = "ioiisdfoipsd0203kf0k";

        // invalidUser1
        name3 = "Jeff Beach";
        email3 = "say_my_name@amazon"; // no top level domain
        password3 = "ioiisdfoipsd0203kf0k";

        // post and authenticate user for get, patch, delete tests
        name4 = "WallE";
        email4 = "shy_robot@ai.net";
        password4 = "9d9d8f0s0dfmsmdfpsdopASFASD)0a9s";

        UserPostDto dummyUserPostDto = new UserPostDto(name4, email4, password4);
        MvcResult dummyResult =
                mockMvc.perform(
                                post("/users")
                                        .content(objectMapper.writeValueAsString(dummyUserPostDto))
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn();
        String dummyResponse = dummyResult.getResponse().getContentAsString();
        id4 = JsonPath.parse(dummyResponse).read("$.id");

        AuthenticationPostDto authenticationPostDto4 = new AuthenticationPostDto(email4, password4);
        MvcResult postAuthenticationResult4 =
                mockMvc.perform(
                                post("/authentications")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(authenticationPostDto4)))
                        .andExpect(status().isOk())
                        .andReturn();
        jwt4 = "Bearer " + Objects.requireNonNull(postAuthenticationResult4.getResponse().getContentAsString());
    }

    @AfterEach
    public void tearDown() {
        name0 = null;
        email0 = null;
        password0 = null;

        name1 = null;
        email1 = null;
        password1 = null;

        name2 = null;
        email2 = null;
        password2 = null;

        name3 = null;
        email3 = null;
        password3 = null;

        id4 = null;
        name4 = null;
        email4 = null;
        password4 = null;
        jwt4 = null;

        userRepository.deleteAll();
    }

    @Test
    public void postValidUserTests() throws Exception {

        // validUser0
        eventCatcher.catchEventOfType(UserCreatedEvent.class);
        UserPostDto validUser0PostDto = new UserPostDto(name0, email0, password0);
        MvcResult result0 =
                mockMvc.perform(
                                post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(validUser0PostDto)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.name").value(validUser0PostDto.getName()))
                        .andExpect(jsonPath("$.email").value(validUser0PostDto.getEmail()))
                        .andReturn();

        // test instance
        String response0 = result0.getResponse().getContentAsString();
        String tempId0 = JsonPath.parse(response0).read("$.id");
        User user0 = userDomainService.getUserById(UUID.fromString(tempId0));
        assertEquals(user0.getName(), validUser0PostDto.getName());
        assertEquals(user0.getEmail(), EmailAddress.fromString(validUser0PostDto.getEmail()));

        // test UserCreatedEvent
        await().until(eventCatcher::hasCaughtEvent);
        UserCreatedEvent userCreatedEvent = (UserCreatedEvent) eventCatcher.getEvent();
        assertEquals(user0.getId(), userCreatedEvent.getUserId());
        assertEquals(name0, userCreatedEvent.getName());
        assertEquals(email0, userCreatedEvent.getEmailAddress().toString());

        // validUser1
        eventCatcher.catchEventOfType(UserCreatedEvent.class);
        UserPostDto validUser1PostDto = new UserPostDto(name1, email1, password1);
        MvcResult result1 =
                mockMvc.perform(
                                post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(validUser1PostDto)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.name").value(validUser1PostDto.getName()))
                        .andExpect(jsonPath("$.email").value(validUser1PostDto.getEmail()))
                        .andReturn();

        // test instance
        String response1 = result1.getResponse().getContentAsString();
        String tempId1 = JsonPath.parse(response1).read("$.id");
        User user1 = userDomainService.getUserById(UUID.fromString(tempId1));
        assertEquals(user1.getName(), validUser1PostDto.getName());
        assertEquals(user1.getEmail(), EmailAddress.fromString(validUser1PostDto.getEmail()));

        // test UserCreatedEvent
        await().until(eventCatcher::hasCaughtEvent);
        UserCreatedEvent userCreatedEvent1 = (UserCreatedEvent) eventCatcher.getEvent();
        assertEquals(user1.getId(), userCreatedEvent1.getUserId());
        assertEquals(name1, userCreatedEvent1.getName());
        assertEquals(email1, userCreatedEvent1.getEmailAddress().toString());
    }

    @Test
    public void postInvalidUserTests() throws Exception {

        // invalidUser0
        UserPostDto invalidUser0PostDto = new UserPostDto(name2, email2, password2);
        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidUser0PostDto)))
                .andExpect(status().isBadRequest());


        // invalidUser1
        UserPostDto invalidUser1PostDto = new UserPostDto(name3, email3, password3);
        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidUser1PostDto)))
                .andExpect(status().isBadRequest());

        // invalidUser2
        UserPostDto invalidUser2PostDto = new UserPostDto(null, email0, password0);
        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidUser2PostDto)))
                .andExpect(status().isBadRequest());

        // invalidUser3
        UserPostDto invalidUser3PostDto = new UserPostDto(name0, null, password0);
        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidUser3PostDto)))
                .andExpect(status().isBadRequest());

        // invalidUser4
        UserPostDto invalidUser4PostDto = new UserPostDto(name0, email0, null);
        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidUser4PostDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void postDuplicateUserTest() throws Exception {
        // duplicate email, email4 already exists because of buildUp()
        UserPostDto duplicatePostDto = new UserPostDto(name0, email4, password0);
        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(duplicatePostDto)))
                .andExpect(status().isConflict());
    }

    @Test
    public void getUserByIdTest() throws Exception {
        MvcResult result =
                mockMvc.perform(
                                get("/users/" + id4)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", jwt4))
                        .andExpect(status().isOk())
                        .andReturn();
        String response = result.getResponse().getContentAsString();

        assertEquals(id4, JsonPath.parse(response).read("$.id"));
        assertEquals(name4, JsonPath.parse(response).read("$.name"));
        assertEquals(email4, JsonPath.parse(response).read("$.email"));
    }

    @Test
    public void getUserByWrongIdTest() throws Exception {
        MvcResult result =
                mockMvc.perform(
                                get("/users/" + UUID.randomUUID())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", jwt4))
                        .andExpect(status().isNotFound())
                        .andReturn();
    }

    @Test
    public void getUserByEmailQueryTest() throws Exception {
        MvcResult result =
                mockMvc.perform(
                                get("/users" )
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", jwt4)
                                        .queryParam("email", email4))
                        .andExpect(status().isOk())
                        .andReturn();
        String response = result.getResponse().getContentAsString();

        assertEquals(id4, JsonPath.parse(response).read("$.id"));
        assertEquals(name4, JsonPath.parse(response).read("$.name"));
        assertEquals(email4, JsonPath.parse(response).read("$.email"));
    }

    @Test
    public void getUserByWrongEmailQueryTest() throws Exception {
        MvcResult result =
                mockMvc.perform(
                                get("/users" )
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", jwt4)
                                        .queryParam("email", "hola.quetal@espanol.com"))
                        .andExpect(status().isNotFound())
                        .andReturn();
    }

    @Test
    public void getOtherUserByEmailQueryTest() throws Exception {
        UserPostDto validUser0PostDto = new UserPostDto(name0, email0, password0);
        MvcResult result0 =
                mockMvc.perform(
                                post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(validUser0PostDto)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.name").value(validUser0PostDto.getName()))
                        .andExpect(jsonPath("$.email").value(validUser0PostDto.getEmail()))
                        .andReturn();
        String response0 = result0.getResponse().getContentAsString();
        String id0 = JsonPath.parse(response0).read("$.id");

        MvcResult result =
                mockMvc.perform(
                                get("/users" )
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", jwt4)
                                        .queryParam("email", email0))
                        .andExpect(status().isOk())
                        .andReturn();
        String response = result.getResponse().getContentAsString();

        assertEquals(id0, JsonPath.parse(response).read("$.id"));
        assertEquals(name0, JsonPath.parse(response).read("$.name"));
        assertEquals(email0, JsonPath.parse(response).read("$.email"));
    }

    @Test
    public void patchUserNameTest() throws Exception {
        eventCatcher.catchEventOfType(UserPatchedEvent.class);
        UserPatchDto userPatchDto = new UserPatchDto("Donald Trump", null);
        MvcResult result =
                mockMvc.perform(
                                patch("/users/" + id4 )
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userPatchDto))
                                        .header("Authorization", jwt4))
                        .andExpect(status().isNoContent())
                        .andReturn();

        // test instance
        User user4 = userDomainService.getUserById(UUID.fromString(id4));
        assertEquals(user4.getName(), userPatchDto.getName());
        assertEquals(user4.getEmail().toString(), email4);

        // test UserPatchedEvent
        await().until(eventCatcher::hasCaughtEvent);
        UserPatchedEvent userPatchedEvent =(UserPatchedEvent) eventCatcher.getEvent();
        assertEquals(user4.getId(), userPatchedEvent.getUserId());
        assertEquals(userPatchDto.getName(), userPatchedEvent.getName());
        assertEquals(user4.getEmail().toString(), email4);
    }

    @Test
    public void patchUserEmailTest() throws Exception {
        eventCatcher.catchEventOfType(UserPatchedEvent.class);
        UserPatchDto userPatchDto = new UserPatchDto(null, "agent.orange@truth.net");
        MvcResult result =
                mockMvc.perform(
                                patch("/users/" + id4 )
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userPatchDto))
                                        .header("Authorization", jwt4))
                        .andExpect(status().isNoContent())
                        .andReturn();

        // test instance
        User user4 = userDomainService.getUserById(UUID.fromString(id4));
        assertEquals(user4.getName(), name4);
        assertEquals(user4.getEmail().toString(), userPatchDto.getEmail());

        // test UserPatchedEvent
        await().until(eventCatcher::hasCaughtEvent);
        UserPatchedEvent userPatchedEvent = (UserPatchedEvent) eventCatcher.getEvent();
        assertEquals(user4.getId(), userPatchedEvent.getUserId());
        assertEquals(user4.getName(), name4);
        assertEquals(user4.getEmail().toString(), userPatchDto.getEmail());
    }

    @Test
    public void patchUserNameAndEmailTest() throws Exception {
        eventCatcher.catchEventOfType(UserPatchedEvent.class);
        UserPatchDto userPatchDto = new UserPatchDto("Donald Trump", "agent.orange@truth.net");
        MvcResult result =
                mockMvc.perform(
                                patch("/users/" + id4 )
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userPatchDto))
                                        .header("Authorization", jwt4))
                        .andExpect(status().isNoContent())
                        .andReturn();

        // test instance
        User user4 = userDomainService.getUserById(UUID.fromString(id4));
        assertEquals(user4.getName(), userPatchDto.getName());
        assertEquals(user4.getEmail(), EmailAddress.fromString(userPatchDto.getEmail()));

        // test UserPatchedEvent
        await().until(eventCatcher::hasCaughtEvent);
        UserPatchedEvent userPatchedEvent = (UserPatchedEvent) eventCatcher.getEvent();
        assertEquals(user4.getId(), userPatchedEvent.getUserId());
        assertEquals(userPatchDto.getName(), userPatchedEvent.getName());
        assertEquals(userPatchDto.getEmail(), userPatchedEvent.getEmailAddress().toString());
    }

    @Test
    public void patchUserWithNullDtoEmailTest() throws Exception {
        MvcResult result =
                mockMvc.perform(
                                patch("/users/" + id4)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", jwt4))
                        .andExpect(status().isBadRequest())
                        .andReturn();
    }

    @Test
    public void patchUserWithFaultyEmailTest() throws Exception {
        UserPatchDto userPatchDto = new UserPatchDto("Donald Trump", "agent.orangetruth.net"); // no @
        MvcResult result =
                mockMvc.perform(
                                patch("/users/" + id4)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userPatchDto))
                                        .header("Authorization", jwt4))
                        .andExpect(status().isBadRequest())
                        .andReturn();
    }

    @Test
    public void patchWrongUserTest() throws Exception {
        UserPostDto validUser0PostDto = new UserPostDto(name0, email0, password0);
        MvcResult result0 =
                mockMvc.perform(
                                post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(validUser0PostDto)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.name").value(validUser0PostDto.getName()))
                        .andExpect(jsonPath("$.email").value(validUser0PostDto.getEmail()))
                        .andReturn();
        String response0 = result0.getResponse().getContentAsString();
        String id0 = JsonPath.parse(response0).read("$.id");

        UserPatchDto userPatchDto = new UserPatchDto("Donald Trump", "agent.orange@truth.net");
        MvcResult result1 =
                mockMvc.perform(
                                patch("/users/" + id0) // wrong but existing user
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userPatchDto))
                                        .header("Authorization", jwt4))
                        .andExpect(status().isForbidden())
                        .andReturn();

        MvcResult result2 =
                mockMvc.perform(
                                patch("/users/" + UUID.randomUUID()) // non-existing user
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userPatchDto))
                                        .header("Authorization", jwt4))
                        .andExpect(status().isForbidden())
                        .andReturn();
    }

    @Test
    public void patchUserUnauthorizedTest() throws Exception {
        UserPatchDto userPatchDto = new UserPatchDto("Donald Trump", "agent.orange@truth.net");
        MvcResult result =
                mockMvc.perform(
                                patch("/users/" + id4)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userPatchDto))) // no jwt
                        .andExpect(status().isUnauthorized())
                        .andReturn();
    }

    @Test
    public void deleteUserTest() throws Exception {
        // validUser1
        eventCatcher.catchEventOfType(UserDeletedEvent.class);
        MvcResult result1 =
                mockMvc.perform(
                                delete("/users/" + id4)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", jwt4))
                        .andExpect(status().isNoContent())
                        .andReturn();

        // test instance
        assertThrows(NoUserFoundException.class, () -> userDomainService.getUserById(UUID.fromString(id4)));

        // test UserDeletedEvent
        await().until(eventCatcher::hasCaughtEvent);
        UserDeletedEvent userDeletedEvent = (UserDeletedEvent) eventCatcher.getEvent();
        assertEquals(userDeletedEvent.getUserId(), UUID.fromString(id4));
    }

    @Test
    public void deleteUserUnauthorizedTest() throws Exception {
        // validUser1
        eventCatcher.catchEventOfType(UserDeletedEvent.class);
        MvcResult result1 =
                mockMvc.perform(
                                delete("/users/" + id4)
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isUnauthorized())
                        .andReturn();

        // test instance
        User user = userDomainService.getUserById(UUID.fromString(id4));

        // test if UserDeletedEvent was thrown
        try {
            await().atMost(3, TimeUnit.SECONDS).until(eventCatcher::hasCaughtEvent);
            fail();
        } catch (Exception e) {
            // test passed
        }
    }

    @Test
    public void deleteNonExistingUserTest() throws Exception {
        // validUser1
        eventCatcher.catchEventOfType(UserDeletedEvent.class);
        MvcResult result1 =
                mockMvc.perform(
                                delete("/users/" + UUID.randomUUID())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", jwt4))
                        .andExpect(status().isForbidden())
                        .andReturn();

        // test if UserDeletedEvent was thrown
        try {
            await().atMost(3, TimeUnit.SECONDS).until(eventCatcher::hasCaughtEvent);
            fail();
        } catch (Exception e) {
            // test passed
        }
    }

    @Test
    public void deleteOtherExistingUserTest() throws Exception {
        // post other user
        UserPostDto dummyUserPostDto = new UserPostDto(name0, email0, password0);
        MvcResult dummyResult =
                mockMvc.perform(
                                post("/users")
                                        .content(objectMapper.writeValueAsString(dummyUserPostDto))
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn();
        String dummyResponse = dummyResult.getResponse().getContentAsString();
        String id0 = JsonPath.parse(dummyResponse).read("$.id");

        // but use jwt from the wrong user
        eventCatcher.catchEventOfType(UserDeletedEvent.class);
        MvcResult result1 =
                mockMvc.perform(
                                delete("/users/" + id0)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", jwt4))
                        .andExpect(status().isForbidden())
                        .andReturn();

        // test if UserDeletedEvent was thrown
        try {
            await().atMost(3, TimeUnit.SECONDS).until(eventCatcher::hasCaughtEvent);
            fail();
        } catch (Exception e) {
            // test passed
        }
    }
}
