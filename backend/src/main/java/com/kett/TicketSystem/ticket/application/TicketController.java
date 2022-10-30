package com.kett.TicketSystem.ticket.application;

import com.kett.TicketSystem.application.TicketSystemService;
import com.kett.TicketSystem.ticket.domain.exceptions.NoTicketFoundException;
import com.kett.TicketSystem.ticket.domain.exceptions.TicketException;
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
@RequestMapping("/tickets")
public class TicketController {
    private final TicketSystemService ticketSystemService;

    @Autowired
    public TicketController(TicketSystemService ticketSystemService) {
        this.ticketSystemService = ticketSystemService;
    }


    // exception handlers

    @ExceptionHandler(TicketException.class)
    public ResponseEntity<String> handleTicketException(TicketException ticketException) {
        return new ResponseEntity<>(ticketException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoTicketFoundException.class)
    public ResponseEntity<String> handleNoTicketFoundException(NoTicketFoundException noTicketFoundException) {
        return new ResponseEntity<>(noTicketFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }
}
