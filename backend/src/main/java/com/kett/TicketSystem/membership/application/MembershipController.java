package com.kett.TicketSystem.membership.application;

import com.kett.TicketSystem.application.TicketSystemService;
import com.kett.TicketSystem.membership.application.dto.MembershipResponseDto;
import com.kett.TicketSystem.membership.domain.exceptions.MembershipException;
import com.kett.TicketSystem.membership.domain.exceptions.NoMembershipFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Transactional
@CrossOrigin(origins = {"http://127.0.0.1:5173"})
@RequestMapping("/memberships")
public class MembershipController {
    private final TicketSystemService ticketSystemService;

    @Autowired
    public MembershipController(TicketSystemService ticketSystemService) {
        this.ticketSystemService = ticketSystemService;
    }


    // endpoints

    @GetMapping("/{id}")
    public ResponseEntity<MembershipResponseDto> getMembershipById(@PathVariable UUID id) {
        MembershipResponseDto membershipResponseDto = ticketSystemService.getMemberShipById(id);
        return new ResponseEntity<>(membershipResponseDto, HttpStatus.OK);
    }


    // exception handlers

    @ExceptionHandler(MembershipException.class)
    public ResponseEntity<String> handleMembershipException(MembershipException membershipException) {
        return new ResponseEntity<>(membershipException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoMembershipFoundException.class)
    public ResponseEntity<String> handleNoMembershipFoundException(NoMembershipFoundException noMembershipFoundException) {
        return new ResponseEntity<>(noMembershipFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }
}
