package com.kett.TicketSystem.user.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import com.kett.TicketSystem.user.application.dto.UserPostDto;
import com.kett.TicketSystem.user.domain.User;
import com.kett.TicketSystem.user.domain.events.UserCreatedEvent;
import com.kett.TicketSystem.user.repository.UserRepository;
import com.kett.TicketSystem.util.DummyEventListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerTests {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final UserService userService;
    private DummyEventListener dummyEventListener;

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


    @Autowired
    public UserControllerTests(MockMvc mockMvc, ObjectMapper objectMapper, UserRepository userRepository, UserService userService) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @BeforeEach
    public void buildUp() throws Exception {
        this.dummyEventListener = new DummyEventListener();

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
    }

    @AfterEach
    public void tearDown() {
        this.dummyEventListener = null;

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

        userRepository.deleteAll();
    }

    @Test
    public void postValidUserTests() throws Exception {

        // validUser0
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
        String tempId0 = JsonPath.parse(response0).read("$.id");
        User user0 = userService.getUserById(UUID.fromString(tempId0));
        assertEquals(user0.getName(), validUser0PostDto.getName());
        assertEquals(user0.getEmail(), EmailAddress.fromString(validUser0PostDto.getEmail()));

        // test UserCreatedEvent
        Optional<UserCreatedEvent> event0 = dummyEventListener.getLatestUserCreatedEvent();
        assertTrue(event0.isPresent());
        Optional<UserCreatedEvent> emptyEvent0 = dummyEventListener.getLatestUserCreatedEvent();
        assertTrue(emptyEvent0.isEmpty()); // check if only one event was thrown

        UserCreatedEvent userCreatedEvent = event0.get();
        assertEquals(user0.getId(), userCreatedEvent.getUserId());
        assertEquals(name0, userCreatedEvent.getName());
        assertEquals(email0, userCreatedEvent.getEmailAddress().toString());


        // validUser1
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
        String response1 = result1.getResponse().getContentAsString();
        String tempId1 = JsonPath.parse(response1).read("$.id");
        User user1 = userService.getUserById(UUID.fromString(tempId1));
        assertEquals(user1.getName(), validUser1PostDto.getName());
        assertEquals(user1.getEmail(), EmailAddress.fromString(validUser1PostDto.getEmail()));

        // test UserCreatedEvent
        Optional<UserCreatedEvent> event1 = dummyEventListener.getLatestUserCreatedEvent();
        assertTrue(event1.isPresent());
        Optional<UserCreatedEvent> emptyEvent1 = dummyEventListener.getLatestUserCreatedEvent();
        assertTrue(emptyEvent1.isEmpty()); // check if only one event was thrown

        UserCreatedEvent userCreatedEvent1 = event1.get();
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
}
