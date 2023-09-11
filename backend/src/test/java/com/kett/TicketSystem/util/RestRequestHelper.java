package com.kett.TicketSystem.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.kett.TicketSystem.authentication.application.dto.AuthenticationPostDto;
import com.kett.TicketSystem.membership.application.dto.MembershipPostDto;
import com.kett.TicketSystem.membership.application.dto.MembershipPutStateDto;
import com.kett.TicketSystem.membership.domain.Role;
import com.kett.TicketSystem.membership.domain.State;
import com.kett.TicketSystem.phase.application.dto.PhasePostDto;
import com.kett.TicketSystem.project.application.dto.ProjectPatchDto;
import com.kett.TicketSystem.project.application.dto.ProjectPostDto;
import com.kett.TicketSystem.ticket.application.dto.TicketPatchDto;
import com.kett.TicketSystem.ticket.application.dto.TicketPostDto;
import com.kett.TicketSystem.user.application.dto.UserPostDto;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.util.List;
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

    public UUID postPhase(String jwt, UUID projectId, String phaseName, UUID previousPhaseId) throws Exception {
        PhasePostDto phasePostDto0 = new PhasePostDto(projectId, phaseName, previousPhaseId);
        MvcResult postResult0 =
                mockMvc.perform(
                                post("/phases")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(phasePostDto0))
                                        .header("Authorization", jwt))
                        .andExpect(status().isCreated())
                        .andReturn();
        String postResponse0 = postResult0.getResponse().getContentAsString();
        return UUID.fromString(JsonPath.parse(postResponse0).read("$.id"));
    }
    public String getPhasesByProjectIdAsJson(String jwt, UUID projectId) throws Exception {
        MvcResult getResult =
                mockMvc.perform(
                                get("/phases")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .queryParam("project-id", projectId.toString())
                                        .header("Authorization", jwt))
                        .andExpect(status().isOk())
                        .andReturn();
        return getResult.getResponse().getContentAsString();
    }

    public UUID postMembership(String jwt, UUID projectId, UUID userId, Role role) throws Exception{
        MembershipPostDto membershipPostDto = new MembershipPostDto(projectId, userId, role);
        MvcResult postResult =
                mockMvc.perform(
                                post("/memberships")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(membershipPostDto))
                                        .header("Authorization", jwt))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.projectId").value(membershipPostDto.getProjectId().toString()))
                        .andExpect(jsonPath("$.userId").value(membershipPostDto.getUserId().toString()))
                        .andExpect(jsonPath("$.role").value(membershipPostDto.getRole().toString()))
                        .andExpect(jsonPath("$.state").value(State.OPEN.toString()))
                        .andReturn();
        String postResponse = postResult.getResponse().getContentAsString();
        return UUID.fromString(JsonPath.parse(postResponse).read("$.id"));
    }

    public void putMembershipState(String jwt, UUID membershipId, State newState) throws Exception {
        MembershipPutStateDto membershipPutStateDto = new MembershipPutStateDto(newState);
        MvcResult putResult =
                mockMvc.perform(
                                put("/memberships/" + membershipId + "/state")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(membershipPutStateDto))
                                        .header("Authorization", jwt))
                        .andExpect(status().isNoContent())
                        .andReturn();
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
    public void deleteUser(String jwt, UUID userId) throws Exception {
        MvcResult result1 =
                mockMvc.perform(
                                delete("/users/" + userId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", jwt))
                        .andExpect(status().isNoContent())
                        .andReturn();
    }

    public String authenticateUser(String userEmail, String userPassword) throws Exception {
        AuthenticationPostDto authenticationPostDto4 = new AuthenticationPostDto(userEmail, userPassword);
        MvcResult postAuthenticationResult4 =
                mockMvc.perform(
                                post("/authentications")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(authenticationPostDto4)))
                        .andExpect(status().isOk())
                        .andReturn();
        return "Bearer " + Objects.requireNonNull(postAuthenticationResult4.getResponse().getContentAsString());
    }

    public UUID postProject(String jwt, String projectName, String projectDescription) throws Exception {
        ProjectPostDto projectPostDto = new ProjectPostDto(projectName, projectDescription);
        MvcResult postResult =
                mockMvc.perform(
                                post("/projects")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectPostDto))
                                        .header("Authorization", jwt))
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
                                        .header("Authorization", jwt))
                        .andExpect(status().isNoContent())
                        .andReturn();
    }

    public void deleteProject(String jwt, UUID projectId) throws Exception {
        MvcResult deleteResult =
                mockMvc.perform(
                                delete("/projects/" + projectId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", jwt))
                        .andExpect(status().isNoContent())
                        .andReturn();
    }

    public UUID postTicket(
            String jwt,
            UUID projectId,
            String title,
            String description,
            LocalDateTime dueTime,
            List<UUID> assigneeIds
    ) throws Exception {
        TicketPostDto ticketPostDto = new TicketPostDto(projectId, title, description, dueTime, assigneeIds);
        MvcResult postResult =
                mockMvc.perform(
                                post("/tickets")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(ticketPostDto))
                                        .header("Authorization", jwt))
                        .andExpect(status().isCreated())
                        .andReturn();
        String postResponse = postResult.getResponse().getContentAsString();
        return UUID.fromString(JsonPath.parse(postResponse).read("$.id"));
    }

    public void patchTicket(
            String jwt,
            UUID ticketId,
            String title,
            String description,
            LocalDateTime dueTime,
            UUID newPhaseId,
            List<UUID> assigneeIds
    ) throws Exception {
        TicketPatchDto ticketPatchDto = new TicketPatchDto(title, description, dueTime, newPhaseId, assigneeIds);
        MvcResult patchResult =
                mockMvc.perform(
                                patch("/tickets/" + ticketId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(ticketPatchDto))
                                        .header("Authorization", jwt))
                        .andExpect(status().isNoContent())
                        .andReturn();
    }
}
