package com.kett.TicketSystem.ticket.application;

import com.kett.TicketSystem.application.TicketSystemService;
import com.kett.TicketSystem.application.exceptions.ImpossibleException;
import com.kett.TicketSystem.application.exceptions.NoParametersException;
import com.kett.TicketSystem.application.exceptions.TooManyParametersException;
import com.kett.TicketSystem.membership.domain.exceptions.InvalidProjectMembersException;
import com.kett.TicketSystem.project.domain.exceptions.NoProjectFoundException;
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
@RequestMapping("/tickets")
public class TicketController {
    private final TicketSystemService ticketSystemService;

    @Autowired
    public TicketController(TicketSystemService ticketSystemService) {
        this.ticketSystemService = ticketSystemService;
    }


    // endpoints

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDto> getTicketById(@PathVariable UUID id) {
        TicketResponseDto ticketResponseDto = ticketSystemService.getTicketById(id);
        return new ResponseEntity<>(ticketResponseDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<TicketResponseDto>> getTicketsByQuery(
            @RequestParam(name = "phase-id", required = false) UUID phaseId,
            @RequestParam(name = "assignee-id", required = false) UUID assigneeId
    ) {
        if (phaseId != null && assigneeId != null) {
            throw new TooManyParametersException("cannot query by more than one parameter yet");
        }

        List<TicketResponseDto> ticketResponseDtos;
        if (phaseId != null) {
            ticketResponseDtos = ticketSystemService.getTicketsByPhaseId(phaseId);
        } else if (assigneeId != null) {
            ticketResponseDtos = ticketSystemService.getTicketsByAssigneeId(assigneeId);
        } else {
            throw new NoParametersException("cannot query if no parameters are specified");
        }
        return new ResponseEntity<>(ticketResponseDtos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TicketResponseDto> postTicket(@RequestBody TicketPostDto ticketPostDto) {
        TicketResponseDto ticketResponseDto = ticketSystemService.addTicket(ticketPostDto);
        URI returnURI = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(ticketResponseDto.getId())
                .toUri();

        return ResponseEntity
                .created(returnURI)
                .body(ticketResponseDto);
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

    @ExceptionHandler(TicketException.class)
    public ResponseEntity<String> handleTicketException(TicketException ticketException) {
        return new ResponseEntity<>(ticketException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoTicketFoundException.class)
    public ResponseEntity<String> handleNoTicketFoundException(NoTicketFoundException noTicketFoundException) {
        return new ResponseEntity<>(noTicketFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidProjectMembersException.class)
    public ResponseEntity<String> handleInvalidProjectMembersException(InvalidProjectMembersException invalidProjectMembersException) {
        return new ResponseEntity<>(invalidProjectMembersException.getMessage(), HttpStatus.NOT_FOUND);
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
