package com.kett.TicketSystem.membership.application;

import com.kett.TicketSystem.application.TicketSystemService;
import com.kett.TicketSystem.application.exceptions.NoParametersException;
import com.kett.TicketSystem.application.exceptions.TooManyParametersException;
import com.kett.TicketSystem.domainprimitives.EmailAddress;
import com.kett.TicketSystem.domainprimitives.EmailAddressException;
import com.kett.TicketSystem.membership.application.dto.MembershipPostDto;
import com.kett.TicketSystem.membership.application.dto.MembershipResponseDto;
import com.kett.TicketSystem.membership.domain.exceptions.IllegalStateUpdateException;
import com.kett.TicketSystem.membership.domain.exceptions.MembershipAlreadyExistsException;
import com.kett.TicketSystem.membership.domain.exceptions.MembershipException;
import com.kett.TicketSystem.membership.domain.exceptions.NoMembershipFoundException;
import com.kett.TicketSystem.project.domain.exceptions.NoProjectFoundException;
import com.kett.TicketSystem.user.domain.exceptions.NoUserFoundException;
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
    public ResponseEntity<List<MembershipResponseDto>> getMembershipsByQuery(
            @RequestParam(name = "user-id", required = false) UUID userId,
            @RequestParam(name = "project-id", required = false) UUID projectId,
            @RequestParam(name = "email", required = false) String email
    ) {
        // TODO: What if another parameter gets added? This is too dirty.
        if (userId != null && projectId != null
            || userId != null && email != null
            || projectId != null && email != null) {
            throw new TooManyParametersException("cannot query by more than one parameter yet");
        }

        List<MembershipResponseDto> membershipResponseDtos;
        if (userId != null) {
            membershipResponseDtos = ticketSystemService.getMembershipsByUserId(userId);
        } else if (projectId != null) {
            membershipResponseDtos = ticketSystemService.getMembershipsByProjectId(projectId);
        } else if (email != null) {
            membershipResponseDtos = ticketSystemService.getMembershipsByEmail(EmailAddress.fromString(email));
        } else {
            throw new NoParametersException("cannot query if no parameters are specified");
        }
        return new ResponseEntity<>(membershipResponseDtos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<MembershipResponseDto> postMembership(@RequestBody MembershipPostDto membershipPostDto) {
        MembershipResponseDto membershipResponseDto = ticketSystemService.addMembership(membershipPostDto);
        URI returnURI = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(membershipResponseDto.getId())
                .toUri();

        return ResponseEntity
                .created(returnURI)
                .body(membershipResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMembership(@PathVariable UUID id) {
        ticketSystemService.deleteMembershipById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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

    @ExceptionHandler(MembershipAlreadyExistsException.class)
    public ResponseEntity<String> handleMembershipAlreadyExistsException(MembershipAlreadyExistsException membershipAlreadyExistsException) {
        return new ResponseEntity<>(membershipAlreadyExistsException.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalStateUpdateException.class)
    public ResponseEntity<String> handleIllegalStateUpdateException(IllegalStateUpdateException illegalStateUpdateException) {
        return new ResponseEntity<>(illegalStateUpdateException.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NoProjectFoundException.class)
    public ResponseEntity<String> handleNoProjectFoundException(NoProjectFoundException noProjectFoundException) {
        return new ResponseEntity<>(noProjectFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoUserFoundException.class)
    public ResponseEntity<String> handleTicketException(NoUserFoundException noUserFoundException) {
        return new ResponseEntity<>(noUserFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmailAddressException.class)
    public ResponseEntity<String> handleEMailAddressException(EmailAddressException eMailAddressException) {
        return new ResponseEntity<>(eMailAddressException.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
