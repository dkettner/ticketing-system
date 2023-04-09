package com.kett.TicketSystem.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.kett.TicketSystem.authentication.dto.AuthenticationPostDto;
import com.kett.TicketSystem.project.application.dto.ProjectPatchDto;
import com.kett.TicketSystem.project.application.dto.ProjectPostDto;
import com.kett.TicketSystem.user.application.dto.UserPostDto;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;
import java.util.Objects;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RestRequestHelper {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public RestRequestHelper(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public UUID postUser(String userName, String userEmail, String userPassword) throws Exception {
        UserPostDto userPostDto = new UserPostDto(userName, userEmail, userPassword);
        MvcResult userPostResult =
                mockMvc.perform(
                                post("/users")
                                        .content(objectMapper.writeValueAsString(userPostDto))
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn();
        String userPostResponse = userPostResult.getResponse().getContentAsString();
        return UUID.fromString(JsonPath.parse(userPostResponse).read("$.id"));
    }

    public String authenticateUser(String userEmail, String userPassword) throws Exception {
        AuthenticationPostDto authenticationPostDto4 = new AuthenticationPostDto(userEmail, userPassword);
        MvcResult postAuthenticationResult4 =
                mockMvc.perform(
                                post("/authentication")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(authenticationPostDto4)))
                        .andExpect(status().isOk())
                        .andReturn();
        return Objects.requireNonNull(postAuthenticationResult4.getResponse().getCookie("jwt")).getValue();
    }

    public UUID postProject(String jwt, String projectName, String projectDescription) throws Exception {
        ProjectPostDto projectPostDto = new ProjectPostDto(projectName, projectDescription);
        MvcResult postResult =
                mockMvc.perform(
                                post("/projects")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectPostDto))
                                        .cookie(new Cookie("jwt", jwt)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.name").value(projectPostDto.getName()))
                        .andExpect(jsonPath("$.description").value(projectPostDto.getDescription()))
                        .andReturn();
        String postResponse = postResult.getResponse().getContentAsString();
        return UUID.fromString(JsonPath.parse(postResponse).read("$.id"));
    }

    public void patchProject(String jwt, UUID projectId, String name, String description) throws Exception {
        ProjectPatchDto projectPatchDto = new ProjectPatchDto(name, description);
        MvcResult patchResult =
                mockMvc.perform(
                                patch("/projects/" + projectId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectPatchDto))
                                        .cookie(new Cookie("jwt", jwt)))
                        .andExpect(status().isNoContent())
                        .andReturn();
    }

    public void deleteProject(String jwt, UUID projectId) throws Exception {
        MvcResult deleteResult =
                mockMvc.perform(
                                delete("/projects/" + projectId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .cookie(new Cookie("jwt", jwt)))
                        .andExpect(status().isNoContent())
                        .andReturn();
    }
}
