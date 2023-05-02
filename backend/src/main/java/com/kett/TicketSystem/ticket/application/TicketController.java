package com.kett.TicketSystem.ticket.application;

import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import com.kett.TicketSystem.common.exceptions.NoParametersException;
import com.kett.TicketSystem.common.exceptions.TooManyParametersException;
import com.kett.TicketSystem.ticket.application.dto.TicketPatchDto;
import com.kett.TicketSystem.ticket.application.dto.TicketPostDto;
import com.kett.TicketSystem.ticket.application.dto.TicketResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@Transactional
@CrossOrigin(origins = {"http://localhost:10000"}, allowCredentials = "true")
@RequestMapping("/tickets")
public class TicketController {
    private final TicketApplicationService ticketApplicationService;

    @Autowired
    public TicketController(TicketApplicationService ticketApplicationService) {
        this.ticketApplicationService = ticketApplicationService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDto> getTicketById(@PathVariable UUID id) {
        TicketResponseDto ticketResponseDto = ticketApplicationService.getTicketById(id);
        return new ResponseEntity<>(ticketResponseDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<TicketResponseDto>> getTicketsByQuery(
            @RequestParam(name = "phase-id", required = false) UUID phaseId,
            @RequestParam(name = "assignee-id", required = false) UUID assigneeId,
            @RequestParam(name = "project-id", required = false) UUID projectId
    ) {

        // TODO: use proper check for too many parameters with map
        if (phaseId != null && assigneeId != null) {
            throw new TooManyParametersException("cannot query by more than one parameter yet");
        }

        List<TicketResponseDto> ticketResponseDtos;
        if (phaseId != null) {
            ticketResponseDtos = ticketApplicationService.getTicketsByPhaseId(phaseId);
        } else if (assigneeId != null) {
            ticketResponseDtos = ticketApplicationService.getTicketsByAssigneeId(assigneeId);
        } else if (projectId != null) {
            ticketResponseDtos = ticketApplicationService.getTicketsByProjectId(projectId);
        } else {
            throw new NoParametersException("cannot query if no parameters are specified");
        }

        return new ResponseEntity<>(ticketResponseDtos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TicketResponseDto> postTicket(@RequestBody TicketPostDto ticketPostDto) {
        EmailAddress userEmail = EmailAddress.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        TicketResponseDto ticketResponseDto = ticketApplicationService.addTicket(ticketPostDto, userEmail);
        URI returnURI = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(ticketResponseDto.getId())
                .toUri();

        return ResponseEntity
                .created(returnURI)
                .body(ticketResponseDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchTicket(@PathVariable UUID id, @RequestBody TicketPatchDto ticketPatchDto) {
        ticketApplicationService.patchTicketById(id, ticketPatchDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTicketById(@PathVariable UUID id) {
        ticketApplicationService.deleteTicketById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
