package com.kett.TicketSystem.membership.application;

import com.kett.TicketSystem.application.TicketSystemService;
import com.kett.TicketSystem.application.exceptions.NoParametersException;
import com.kett.TicketSystem.application.exceptions.TooManyParametersException;
import com.kett.TicketSystem.membership.application.dto.MembershipResponseDto;
import com.kett.TicketSystem.membership.domain.exceptions.MembershipException;
import com.kett.TicketSystem.membership.domain.exceptions.NoMembershipFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @GetMapping
    public ResponseEntity<List<MembershipResponseDto>> getMemberShipsByQuery(
            @RequestParam(name = "user-id", required = false) UUID userId,
            @RequestParam(name = "project-id", required = false) UUID projectId
    ) {
        if (userId != null && projectId != null) {
            throw new TooManyParametersException("cannot query by more than one parameter yet");
        }

        List<MembershipResponseDto> membershipResponseDtos;
        if (userId != null) {
            membershipResponseDtos = ticketSystemService.getMembershipsByUserId(userId);
        } else if (projectId != null) {
            membershipResponseDtos = ticketSystemService.getMembershipsByProjectId(projectId);
        } else {
            throw new NoParametersException("cannot query if no parameters are specified");
        }
        return new ResponseEntity<>(membershipResponseDtos, HttpStatus.OK);
    }


    // exception handlers

    @ExceptionHandler(NoParametersException.class)
    public ResponseEntity<String> handleNoParametersException(NoParametersException noParametersException) {
        return new ResponseEntity<>(noParametersException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TooManyParametersException.class)
    public ResponseEntity<String> handleTooManyParametersException(TooManyParametersException tooManyParametersException) {
        return new ResponseEntity<>(tooManyParametersException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MembershipException.class)
    public ResponseEntity<String> handleMembershipException(MembershipException membershipException) {
        return new ResponseEntity<>(membershipException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoMembershipFoundException.class)
    public ResponseEntity<String> handleNoMembershipFoundException(NoMembershipFoundException noMembershipFoundException) {
        return new ResponseEntity<>(noMembershipFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }
}
