package com.kett.TicketSystem.projectphase.application;

import com.kett.TicketSystem.application.TicketSystemService;
import com.kett.TicketSystem.projectphase.domain.exceptions.NoPhaseFoundException;
import com.kett.TicketSystem.projectphase.domain.exceptions.PhaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
