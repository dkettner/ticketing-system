package com.kett.TicketSystem.ticket.application;

import com.kett.TicketSystem.application.TicketSystemService;
import com.kett.TicketSystem.application.exceptions.NoParametersException;
import com.kett.TicketSystem.application.exceptions.TooManyParametersException;
import com.kett.TicketSystem.ticket.application.dto.TicketPatchDto;
import com.kett.TicketSystem.ticket.application.dto.TicketPostDto;
import com.kett.TicketSystem.ticket.application.dto.TicketResponseDto;
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


    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDto> getTicketById(@PathVariable UUID id) {
        TicketResponseDto ticketResponseDto = ticketSystemService.getTicketById(id);
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
            ticketResponseDtos = ticketSystemService.getTicketsByPhaseId(phaseId);
        } else if (assigneeId != null) {
            ticketResponseDtos = ticketSystemService.getTicketsByAssigneeId(assigneeId);
        } else if (projectId != null) {
            ticketResponseDtos = ticketSystemService.getTicketsByProjectId(projectId);
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

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchTicket(@PathVariable UUID id, @RequestBody TicketPatchDto ticketPatchDto) {
        ticketSystemService.patchTicketById(id, ticketPatchDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTicketById(@PathVariable UUID id) {
        ticketSystemService.deleteTicketById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
