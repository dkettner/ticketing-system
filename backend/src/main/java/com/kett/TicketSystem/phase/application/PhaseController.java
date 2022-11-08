package com.kett.TicketSystem.phase.application;

import com.kett.TicketSystem.application.TicketSystemService;
import com.kett.TicketSystem.application.exceptions.ImpossibleException;
import com.kett.TicketSystem.application.exceptions.NoParametersException;
import com.kett.TicketSystem.phase.application.dto.PhasePostDto;
import com.kett.TicketSystem.phase.application.dto.PhaseResponseDto;
import com.kett.TicketSystem.phase.domain.exceptions.LastPhaseException;
import com.kett.TicketSystem.phase.domain.exceptions.NoPhaseFoundException;
import com.kett.TicketSystem.phase.domain.exceptions.PhaseException;
import com.kett.TicketSystem.project.domain.exceptions.NoProjectFoundException;
import com.kett.TicketSystem.project.domain.exceptions.PhaseIsNotEmptyException;
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
@RequestMapping("/phases")
public class PhaseController {
    private final TicketSystemService ticketSystemService;

    @Autowired
    public PhaseController(TicketSystemService ticketSystemService) {
        this.ticketSystemService = ticketSystemService;
    }


    // endpoints

    @GetMapping("/{id}")
    public ResponseEntity<PhaseResponseDto> getPhaseById(@PathVariable UUID id) {
        PhaseResponseDto phaseResponseDto = ticketSystemService.getPhaseById(id);
        return new ResponseEntity<>(phaseResponseDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<PhaseResponseDto>> getPhasesByQuery(
            @RequestParam(name = "project-id", required = true) UUID projectId
    ) {
        if (projectId == null) { // TODO: null check not needed?
            throw new NoParametersException("cannot query if no projectId is specified");
        }

        List<PhaseResponseDto> phaseResponseDtos = ticketSystemService.getPhasesByProjectId(projectId);
        return new ResponseEntity<>(phaseResponseDtos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PhaseResponseDto> postPhase(@RequestBody PhasePostDto phasePostDto) {
        PhaseResponseDto phaseResponseDto = ticketSystemService.addPhaseAuthorized(phasePostDto);
        URI returnURI = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(phaseResponseDto.getId())
                .toUri();

        return ResponseEntity
                .created(returnURI)
                .body(phaseResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePhase(@PathVariable UUID id) {
        ticketSystemService.deletePhaseById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    // exception handlers

    @ExceptionHandler(NoParametersException.class)
    public ResponseEntity<String> handleNoParametersException(NoParametersException noParametersException) {
        return new ResponseEntity<>(noParametersException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PhaseException.class)
    public ResponseEntity<String> handlePhaseException(PhaseException phaseException) {
        return new ResponseEntity<>(phaseException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoPhaseFoundException.class)
    public ResponseEntity<String> handleNoPhaseFoundException(NoPhaseFoundException noPhaseFoundException) {
        return new ResponseEntity<>(noPhaseFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PhaseIsNotEmptyException.class)
    public ResponseEntity<String> handlePhaseIsNotEmptyException(PhaseIsNotEmptyException phaseIsNotEmptyException) {
        return new ResponseEntity<>(phaseIsNotEmptyException.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(LastPhaseException.class)
    public ResponseEntity<String> handleLastPhaseException(LastPhaseException lastPhaseException) {
        return new ResponseEntity<>(lastPhaseException.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NoProjectFoundException.class)
    public ResponseEntity<String> handleNoProjectFoundException(NoProjectFoundException noProjectFoundException) {
        return new ResponseEntity<>(noProjectFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ImpossibleException.class)
    public ResponseEntity<String> handleImpossibleException(ImpossibleException impossibleException) {
        return new ResponseEntity<>(impossibleException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
