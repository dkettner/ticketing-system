package com.kett.TicketSystem.project.application;

import com.kett.TicketSystem.application.TicketSystemService;

import com.kett.TicketSystem.project.application.dto.*;
import com.kett.TicketSystem.project.domain.exceptions.*;
import com.kett.TicketSystem.ticket.application.dto.TicketPatchDto;
import com.kett.TicketSystem.ticket.application.dto.TicketPostDto;
import com.kett.TicketSystem.ticket.application.dto.TicketResponseDto;
import com.kett.TicketSystem.ticket.domain.exceptions.NoTicketFoundException;
import com.kett.TicketSystem.ticket.domain.exceptions.TicketException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@Transactional
@CrossOrigin(origins = {"http://127.0.0.1:5173"})
@RequestMapping("/projects")
public class ProjectController {
    private final TicketSystemService ticketSystemService;

    @Autowired
    public ProjectController(TicketSystemService ticketSystemService) {
        this.ticketSystemService = ticketSystemService;
    }

    // rest endpoints

    // TODO: delete this endpoint, it is only for testing purposes
    @GetMapping
    public ResponseEntity<List<ProjectResponseDto>> getProjectById() {
        List<ProjectResponseDto> projectResponseDto = ticketSystemService.fetchAllProjects();
        return new ResponseEntity<>(projectResponseDto, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> getProjectById(@PathVariable UUID id) {
        ProjectResponseDto projectResponseDto = ticketSystemService.fetchProjectById(id);
        return new ResponseEntity<>(projectResponseDto, HttpStatus.OK);
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

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchProjectById(@PathVariable UUID id, @RequestBody ProjectPatchDto projectPatchDto) {
        ticketSystemService.patchProjectById(id, projectPatchDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteProject(@PathVariable UUID id) { // TODO: What to use instead of Object?
        ticketSystemService.deleteProjectById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/tickets")
    public ResponseEntity<List<TicketResponseDto>> getTicketsByProjectId(@PathVariable UUID id) {
        List<TicketResponseDto> ticketDtos = ticketSystemService.fetchTicketsByProjectId(id);
        return new ResponseEntity<>(ticketDtos, HttpStatus.OK);
    }

    @GetMapping("/{id}/tickets/{ticketNumber}")
    public ResponseEntity<TicketResponseDto> getTicketByProjectIdAndTicketNumber(@PathVariable UUID id, @PathVariable UUID ticketNumber) {
        TicketResponseDto ticketDto = ticketSystemService.fetchTicketByProjectIdAndTicketNumber(id, ticketNumber);
        return new ResponseEntity<>(ticketDto, HttpStatus.OK);
    }

    @PostMapping("/{id}/tickets")
    public ResponseEntity<TicketResponseDto> postTicket(@PathVariable UUID id, @RequestBody TicketPostDto ticketPostDto) {
        TicketResponseDto ticketResponseDto = ticketSystemService.addTicketToProject(id, ticketPostDto);
        URI returnURI = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{ticketNumber}")
                .buildAndExpand(ticketResponseDto.getTicketNumber())
                .toUri();

        return ResponseEntity
                .created(returnURI)
                .body(ticketResponseDto);
    }

    @PatchMapping("/{id}/tickets/{ticketNumber}")
    public ResponseEntity<Object> patchTicket(@PathVariable UUID id, @PathVariable UUID ticketNumber,
                                              @RequestBody TicketPatchDto ticketPatchDto) {
        ticketSystemService.patchTicket(id, ticketNumber, ticketPatchDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}/tickets/{ticketNumber}")
    public ResponseEntity<Object> deleteTicket(@PathVariable UUID id, @PathVariable UUID ticketNumber) {
        ticketSystemService.deleteTicketByProjectIdAndTicketNumber(id, ticketNumber);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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

    @ExceptionHandler(ImpossibleException.class)
    public ResponseEntity<String> handleImpossibleException(ImpossibleException impossibleException) {
        return new ResponseEntity<>(impossibleException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
