package com.kett.TicketSystem.project.application;

import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import com.kett.TicketSystem.project.application.dto.*;
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
@CrossOrigin(origins = {"http://localhost:10000"}, allowCredentials = "true")
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectApplicationService projectApplicationService;

    @Autowired
    public ProjectController(ProjectApplicationService projectApplicationService) {
        this.projectApplicationService = projectApplicationService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> getProjectById(@PathVariable UUID id) {
        ProjectResponseDto projectResponseDto = projectApplicationService.fetchProjectById(id);
        return new ResponseEntity<>(projectResponseDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ProjectResponseDto> postProject(@RequestBody ProjectPostDto projectPostDto) {
        EmailAddress userEmail = EmailAddress.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        ProjectResponseDto projectResponseDto = projectApplicationService.addProject(projectPostDto, userEmail);
        URI returnURI = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(projectResponseDto.getId())
                .toUri();

        return ResponseEntity
                .created(returnURI)
                .body(projectResponseDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchProjectById(@PathVariable UUID id, @RequestBody ProjectPatchDto projectPatchDto) {
        projectApplicationService.patchProjectById(id, projectPatchDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable UUID id) {
        projectApplicationService.deleteProjectById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
