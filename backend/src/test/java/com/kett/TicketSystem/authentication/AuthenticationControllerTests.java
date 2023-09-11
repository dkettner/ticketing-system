package com.kett.TicketSystem.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kett.TicketSystem.authentication.application.dto.AuthenticationPostDto;
import com.kett.TicketSystem.authentication.domain.jwt.JwtTokenProvider;
import com.kett.TicketSystem.user.application.dto.UserPostDto;
import com.kett.TicketSystem.user.repository.UserRepository;
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
import static org.junit.jupiter.api.Assertions.*;

import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles({ "test" })
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthenticationControllerTests {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private String name0;
    private String email0;
    private String password0;
    private String jwt0;

    private String name1;
    private String email1;
    private String password1;
    private String jwt1;

    @Autowired
    public AuthenticationControllerTests(MockMvc mockMvc, ObjectMapper objectMapper, UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @BeforeEach
    public void buildUp() throws Exception {

        // user0
        name0 = "Bill Gates";
        email0 = "dollar.bill@microsoft.com";
        password0 = "hereComesThe_Money1337";

        UserPostDto dummyUserPostDto0 = new UserPostDto(name0, email0, password0);
        MvcResult dummyResult0 =
                mockMvc.perform(
                                post("/users")
                                        .content(objectMapper.writeValueAsString(dummyUserPostDto0))
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn();

        // user1
        name1 = "Jeff Bezos";
        email1 = "say_my_name@amazon.com";
        password1 = "ioiisdfoipsd0203kf0k";

        UserPostDto dummyUserPostDto1 = new UserPostDto(name1, email1, password1);
        MvcResult dummyResult1 =
                mockMvc.perform(
                                post("/users")
                                        .content(objectMapper.writeValueAsString(dummyUserPostDto1))
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn();
    }

    @AfterEach
    public void tearDown() {
        name0 = null;
        email0 = null;
        password0 = null;
        jwt0 = null;

        name1 = null;
        email1 = null;
        password1 = null;
        jwt1 = null;

        userRepository.deleteAll();
    }

    @Test
    public void postValidAuthenticationTest() throws Exception {
        AuthenticationPostDto authenticationPostDto0 = new AuthenticationPostDto(email0, password0);
        MvcResult postAuthenticationResult0 =
                mockMvc.perform(
                                post("/authentications")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(authenticationPostDto0)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$").isNotEmpty())
                        .andReturn();

        jwt0 = Objects.requireNonNull(postAuthenticationResult0.getResponse().getContentAsString());
        assertEquals(email0, jwtTokenProvider.getEmailFromToken(jwt0));
        assertTrue(jwtTokenProvider.validateToken(jwt0));

        AuthenticationPostDto authenticationPostDto1 = new AuthenticationPostDto(email1, password1);
        MvcResult postAuthenticationResult1 =
                mockMvc.perform(
                                post("/authentications")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(authenticationPostDto1)))
                        .andExpect(status().isOk())
                        .andReturn();

        jwt1 = Objects.requireNonNull(postAuthenticationResult1.getResponse().getContentAsString());
        assertEquals(email1, jwtTokenProvider.getEmailFromToken(jwt1));
        assertTrue(jwtTokenProvider.validateToken(jwt1));
    }

    @Test
    public void postInvalidAuthenticationTest() throws Exception {
        AuthenticationPostDto invalidAuthenticationPostDto0 = new AuthenticationPostDto(email0, password1);
        MvcResult postAuthenticationResult0 =
                mockMvc.perform(
                                post("/authentications")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(invalidAuthenticationPostDto0)))
                        .andExpect(status().isUnauthorized())
                        .andReturn();

        AuthenticationPostDto invalidAuthenticationPostDto1 = new AuthenticationPostDto(email1, password0);
        MvcResult postAuthenticationResult1 =
                mockMvc.perform(
                                post("/authentications")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(invalidAuthenticationPostDto1)))
                        .andExpect(status().isUnauthorized())
                        .andReturn();

        AuthenticationPostDto invalidAuthenticationPostDto2 = new AuthenticationPostDto("invalid.email@yahoo.com", password0);
        MvcResult postAuthenticationResult2 =
                mockMvc.perform(
                                post("/authentications")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(invalidAuthenticationPostDto2)))
                        .andExpect(status().isNotFound())
                        .andReturn();
    }
}
