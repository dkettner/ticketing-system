package com.kett.TicketSystem.authentication;

import com.kett.TicketSystem.application.TicketSystemService;
import com.kett.TicketSystem.authentication.dto.AuthenticationPostDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@Transactional
@CrossOrigin(origins = {"http://127.0.0.1:5173"}, allowCredentials = "true")
@RequestMapping("/authentication")
public class AuthenticationController {
    private final TicketSystemService ticketSystemService;

    @Autowired
    public AuthenticationController(TicketSystemService ticketSystemService) {
        this.ticketSystemService = ticketSystemService;
    }


    @PostMapping
    public ResponseEntity<String> authenticateUser(@RequestBody AuthenticationPostDto authenticationPostDto) {
        String jwt = ticketSystemService.authenticateUser(authenticationPostDto);
        return new ResponseEntity<>(jwt, HttpStatus.CREATED);
    }
}
