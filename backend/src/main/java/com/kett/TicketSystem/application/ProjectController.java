package com.kett.TicketSystem.application;

import com.kett.TicketSystem.application.dto.ProjectPostDto;
import com.kett.TicketSystem.application.dto.ProjectResponseDto;

import com.kett.TicketSystem.application.dto.TicketResponseDto;
import com.kett.TicketSystem.project.domain.exceptions.NoTicketFoundException;
import com.kett.TicketSystem.project.domain.exceptions.ProjectException;
import com.kett.TicketSystem.project.domain.exceptions.NoProjectFoundException;
import com.kett.TicketSystem.project.domain.exceptions.TicketException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects")
public class ProjectController {
    private final TicketSystemService ticketSystemService;

    @Autowired
    public ProjectController(TicketSystemService ticketSystemService) {
        this.ticketSystemService = ticketSystemService;
    }

    // rest endpoints

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> getProjectById(@PathVariable UUID id) {
        ProjectResponseDto projectResponseDto = ticketSystemService.fetchProjectById(id);
        return new ResponseEntity<>(projectResponseDto, HttpStatus.OK);
    }

    @GetMapping("/{id}/tickets")
    public ResponseEntity<List<TicketResponseDto>> getTicketByProjectId(@PathVariable UUID id) {
        List<TicketResponseDto> ticketDtos = ticketSystemService.fetchTicketsByProjectId(id);
        return new ResponseEntity<>(ticketDtos, HttpStatus.OK);
    }

    @GetMapping("/{id}/tickets/{ticketNumber}")
    public ResponseEntity<TicketResponseDto> getTicketByProjectIdAndTicketNumber(@PathVariable UUID id, @PathVariable UUID ticketNumber) {
        TicketResponseDto ticketDto = ticketSystemService.fetchTicketByProjectIdAndTicketNumber(id, ticketNumber);
        return new ResponseEntity<>(ticketDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ProjectResponseDto> postProject(@RequestBody ProjectPostDto projectPostDto) {
        ProjectResponseDto projectResponseDto = ticketSystemService.addProject(projectPostDto);
        URI returnURI = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(projectResponseDto.getId())
                .toUri();

        return ResponseEntity
                .created(returnURI)
                .body(projectResponseDto);
    }

    // exception handlers

    @ExceptionHandler(ProjectException.class)
    public ResponseEntity<String> handleProjectException(ProjectException projectException) {
        return new ResponseEntity<>(projectException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoProjectFoundException.class)
    public ResponseEntity<String> handleNoProjectFoundException(NoProjectFoundException noProjectFoundException) {
        return new ResponseEntity<>(noProjectFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TicketException.class)
    public ResponseEntity<String> handleTicketException(TicketException ticketException) {
        return new ResponseEntity<>(ticketException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoTicketFoundException.class)
    public ResponseEntity<String> handleNoTicketFoundException(NoTicketFoundException noTicketFoundException) {
        return new ResponseEntity<>(noTicketFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }
}
