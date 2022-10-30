package com.kett.TicketSystem.phase.application;

import com.kett.TicketSystem.application.TicketSystemService;
import com.kett.TicketSystem.phase.application.dto.PhaseResponseDto;
import com.kett.TicketSystem.phase.domain.exceptions.NoPhaseFoundException;
import com.kett.TicketSystem.phase.domain.exceptions.PhaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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


    // exception handlers

    @ExceptionHandler(PhaseException.class)
    public ResponseEntity<String> handlePhaseException(PhaseException phaseException) {
        return new ResponseEntity<>(phaseException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoPhaseFoundException.class)
    public ResponseEntity<String> handleNoPhaseFoundException(NoPhaseFoundException noPhaseFoundException) {
        return new ResponseEntity<>(noPhaseFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }
}
