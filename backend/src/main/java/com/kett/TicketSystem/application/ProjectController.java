package com.kett.TicketSystem.application;

import com.kett.TicketSystem.application.dto.ProjectResponseDto;

import com.kett.TicketSystem.project.domain.exceptions.ProjectException;
import com.kett.TicketSystem.project.domain.exceptions.NoProjectFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/projects")
public class ProjectController {
    private final TicketSystemService ticketSystemService;

    @Autowired
    public ProjectController(TicketSystemService ticketSystemService) {
        this.ticketSystemService = ticketSystemService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> getProjectDtoById(@PathVariable UUID id) {
        ProjectResponseDto projectResponseDto = ticketSystemService.fetchProjectResponseDtoById(id);
        return new ResponseEntity<>(projectResponseDto, HttpStatus.OK);
    }


    @ExceptionHandler(ProjectException.class)
    public ResponseEntity<String> handleProjectException(ProjectException projectException) {
        return new ResponseEntity<>(projectException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoProjectFoundException.class)
    public ResponseEntity<String> handleNoProjectFoundException(NoProjectFoundException noProjectFoundException) {
        return new ResponseEntity<>(noProjectFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }
}
