package com.kett.TicketSystem.project.application;

import com.kett.TicketSystem.application.TicketSystemService;

import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import com.kett.TicketSystem.project.application.dto.*;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@Transactional
@CrossOrigin(origins = {"http://127.0.0.1:5173"}, allowCredentials = "true")
@RequestMapping("/projects")
public class ProjectController {
    private final TicketSystemService ticketSystemService;

    @Autowired
    public ProjectController(TicketSystemService ticketSystemService) {
        this.ticketSystemService = ticketSystemService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> getProjectById(@PathVariable UUID id) {
        MDC.put("transactionId", UUID.randomUUID().toString());
        ProjectResponseDto projectResponseDto = ticketSystemService.fetchProjectById(id);
        MDC.remove("transactionId");
        return new ResponseEntity<>(projectResponseDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ProjectResponseDto> postProject(@RequestBody ProjectPostDto projectPostDto) {
        MDC.put("transactionId", UUID.randomUUID().toString());

        EmailAddress userEmail = EmailAddress.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        ProjectResponseDto projectResponseDto = ticketSystemService.addProject(projectPostDto, userEmail);
        URI returnURI = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(projectResponseDto.getId())
                .toUri();

        MDC.remove("transactionId");
        return ResponseEntity
                .created(returnURI)
                .body(projectResponseDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchProjectById(@PathVariable UUID id, @RequestBody ProjectPatchDto projectPatchDto) {
        MDC.put("transactionId", UUID.randomUUID().toString());
        ticketSystemService.patchProjectById(id, projectPatchDto);
        MDC.remove("transactionId");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable UUID id) {
        MDC.put("transactionId", UUID.randomUUID().toString());
        ticketSystemService.deleteProjectById(id);
        MDC.remove("transactionId");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
