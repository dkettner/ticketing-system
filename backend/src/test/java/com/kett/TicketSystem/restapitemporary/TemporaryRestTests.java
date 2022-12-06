package com.kett.TicketSystem.restapitemporary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.kett.TicketSystem.authentication.dto.AuthenticationPostDto;
import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import com.kett.TicketSystem.membership.application.MembershipService;
import com.kett.TicketSystem.membership.repository.MembershipRepository;
import com.kett.TicketSystem.phase.application.PhaseService;
import com.kett.TicketSystem.phase.repository.PhaseRepository;
import com.kett.TicketSystem.project.application.ProjectService;
import com.kett.TicketSystem.project.repository.ProjectRepository;
import com.kett.TicketSystem.ticket.application.TicketService;
import com.kett.TicketSystem.ticket.repository.TicketRepository;
import com.kett.TicketSystem.user.application.UserService;
import com.kett.TicketSystem.user.application.dto.UserPostDto;
import com.kett.TicketSystem.user.domain.User;
import com.kett.TicketSystem.user.repository.UserRepository;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TemporaryRestTests {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    private final MembershipRepository membershipRepository;
    private final PhaseRepository phaseRepository;
    private final ProjectRepository projectRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    private final MembershipService membershipService;
    private final PhaseService phaseService;
    private final ProjectService projectService;
    private final TicketService ticketService;
    private final UserService userService;

    // first user
    private String id0;
    private String name0;
    private String email0;
    private String password0;
    private String jwt0;

    // second user
    private String id1;
    private String name1;
    private String email1;
    private String password1;
    private String jwt1;

    @Autowired
    public TemporaryRestTests(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            MembershipRepository membershipRepository,
            PhaseRepository phaseRepository,
            ProjectRepository projectRepository,
            TicketRepository ticketRepository,
            UserRepository userRepository,
            MembershipService membershipService,
            PhaseService phaseService,
            ProjectService projectService,
            TicketService ticketService,
            UserService userService) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.phaseRepository = phaseRepository;
        this.membershipRepository = membershipRepository;
        this.membershipService = membershipService;
        this.phaseService = phaseService;
        this.projectService = projectService;
        this.ticketService = ticketService;
        this.userService = userService;
    }

    @BeforeEach
    public void buildUp() throws Exception {

        // post user0
        name0 = "Bill Gates";
        email0 = "dollar.bill@microsoft.com";
        password0 = "==))88hhnsalsdmfsdklfmNOINöoÖ-sd83´230";

        UserPostDto userPostDto0 = new UserPostDto(name0, email0, password0);

        MvcResult postUserResult0 =
                mockMvc.perform(
                            post("/users")
                                    .content(objectMapper.writeValueAsString(userPostDto0))
                                    .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andReturn();

        String postUserResponse0 = postUserResult0.getResponse().getContentAsString();
        id0 = JsonPath.parse(postUserResponse0).read("$.id");

        // post authentication for user0
        AuthenticationPostDto authenticationPostDto0 = new AuthenticationPostDto(email0, password0);
        MvcResult postAuthenticationResult0 =
                mockMvc.perform(
                            post("/authentication")
                                    .content(objectMapper.writeValueAsString(authenticationPostDto0))
                                    .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andReturn();

        jwt0 = postAuthenticationResult0.getResponse().getContentAsString();


        // post user1
        name1 = "Jeff Bezos";
        email1 = "say_my_name@amazon.com";
        password1 = "ioiisdfoipsd0ß2ß03kfß0k..dp.pe.,fs=)(//%$§5";

        UserPostDto userPostDto1 = new UserPostDto(name1, email1, password1);

        MvcResult postUserResult1 =
                mockMvc.perform(
                            post("/users")
                                    .content(objectMapper.writeValueAsString(userPostDto1))
                                    .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andReturn();

        String postUserResponse1 = postUserResult1.getResponse().getContentAsString();
        id1 = JsonPath.parse(postUserResponse1).read("$.id");

        // post authentication for user1
        AuthenticationPostDto authenticationPostDto1 = new AuthenticationPostDto(email1, password1);
        MvcResult postAuthenticationResult1 =
                mockMvc.perform(
                            post("/authentication")
                                    .content(objectMapper.writeValueAsString(authenticationPostDto1))
                                    .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andReturn();

        jwt1 = postAuthenticationResult1.getResponse().getContentAsString();
    }

    @AfterEach
    public void tearDown() {
        id0 = null;
        name0 = null;
        email0 = null;
        password0 = null;
        jwt0 = null;

        id1 = null;
        name1 = null;
        email1 = null;
        password1 = null;
        jwt1 = null;

        membershipRepository.deleteAll();
        phaseRepository.deleteAll();
        projectRepository.deleteAll();
        ticketRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void checkValidPostUser() throws Exception {

        // tempUser0
        String tempName0 = "Elon Musk";
        String tempEmail0 = "elon.musk@tesla.com";
        String tempPassword0 = "getOnMyLevel420";

        UserPostDto tempUserPostDto0 = new UserPostDto(tempName0, tempEmail0, tempPassword0);

        MvcResult result0 =
                mockMvc.perform(
                            post("/users")
                                    .content(objectMapper.writeValueAsString(tempUserPostDto0))
                                    .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.name").value(tempUserPostDto0.getName()))
                        .andExpect(jsonPath("$.email").value(tempUserPostDto0.getEmail()))
                        .andReturn();

        String response0 = result0.getResponse().getContentAsString();
        String tempId0 = JsonPath.parse(response0).read("$.id");
        User user0 = userService.getUserById(UUID.fromString(tempId0));
        assertEquals(user0.getName(), tempUserPostDto0.getName());
        assertEquals(user0.getEmail(), EmailAddress.fromString(tempUserPostDto0.getEmail()));


        // tempUser1
        String tempName1 = "WallE";
        String tempEmail1 = "shy_robot@ai.net";
        String tempPassword1 = "9d9d8f0s0dfmsmdfüpsdopASFASD)0a9s";

        UserPostDto tempUserPostDto1 = new UserPostDto(tempName1, tempEmail1, tempPassword1);

        MvcResult result1 =
                mockMvc.perform(
                            post("/users")
                                    .content(objectMapper.writeValueAsString(tempUserPostDto1))
                                    .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.name").value(tempUserPostDto1.getName()))
                        .andExpect(jsonPath("$.email").value(tempUserPostDto1.getEmail()))
                        .andReturn();

        String response1 = result1.getResponse().getContentAsString();
        String tempId1 = JsonPath.parse(response1).read("$.id");
        User user1 = userService.getUserById(UUID.fromString(tempId1));
        assertEquals(user1.getName(), tempUserPostDto1.getName());
        assertEquals(user1.getEmail(), EmailAddress.fromString(tempUserPostDto1.getEmail()));


        // tempUser2
        String tempName2 = "EVA";
        String tempEmail2 = "eva@ai.net";
        String tempPassword2 = "oijoibLUJBlLub9865657JUBUoobnk09767_:?";

        UserPostDto tempUserPostDto2 = new UserPostDto(tempName2, tempEmail2, tempPassword2);

        MvcResult result2 =
                mockMvc.perform(
                            post("/users")
                                    .content(objectMapper.writeValueAsString(tempUserPostDto2))
                                    .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.name").value(tempUserPostDto2.getName()))
                        .andExpect(jsonPath("$.email").value(tempUserPostDto2.getEmail()))
                        .andReturn();

        String response2 = result2.getResponse().getContentAsString();
        String tempId2 = JsonPath.parse(response2).read("$.id");
        User user2 = userService.getUserById(UUID.fromString(tempId2));
        assertEquals(user2.getName(), tempUserPostDto2.getName());
        assertEquals(user2.getEmail(), EmailAddress.fromString(tempUserPostDto2.getEmail()));
    }
}
