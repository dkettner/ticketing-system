package com.kett.TicketSystem.phase.application;

import com.kett.TicketSystem.application.TicketSystemService;
import com.kett.TicketSystem.common.exceptions.NoParametersException;
import com.kett.TicketSystem.phase.application.dto.PhasePatchNameDto;
import com.kett.TicketSystem.phase.application.dto.PhasePatchPositionDto;
import com.kett.TicketSystem.phase.application.dto.PhasePostDto;
import com.kett.TicketSystem.phase.application.dto.PhaseResponseDto;
import org.slf4j.MDC;
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
@CrossOrigin(origins = {"http://127.0.0.1:5173"}, allowCredentials = "true")
@RequestMapping("/phases")
public class PhaseController {
    private final TicketSystemService ticketSystemService;

    @Autowired
    public PhaseController(TicketSystemService ticketSystemService) {
        this.ticketSystemService = ticketSystemService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<PhaseResponseDto> getPhaseById(@PathVariable UUID id) {
        MDC.put("transactionId", UUID.randomUUID().toString());
        PhaseResponseDto phaseResponseDto = ticketSystemService.getPhaseById(id);
        MDC.remove("transactionId");
        return new ResponseEntity<>(phaseResponseDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<PhaseResponseDto>> getPhasesByQuery(
            @RequestParam(name = "project-id", required = true) UUID projectId
    ) {
        MDC.put("transactionId", UUID.randomUUID().toString());

        if (projectId == null) { // TODO: null check not needed?
            throw new NoParametersException("cannot query if no projectId is specified");
        }
        List<PhaseResponseDto> phaseResponseDtos = ticketSystemService.getPhasesByProjectId(projectId);

        MDC.remove("transactionId");
        return new ResponseEntity<>(phaseResponseDtos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PhaseResponseDto> postPhase(@RequestBody PhasePostDto phasePostDto) {
        MDC.put("transactionId", UUID.randomUUID().toString());

        PhaseResponseDto phaseResponseDto = ticketSystemService.addPhase(phasePostDto);
        URI returnURI = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(phaseResponseDto.getId())
                .toUri();

        MDC.remove("transactionId");
        return ResponseEntity
                .created(returnURI)
                .body(phaseResponseDto);
    }

    @PatchMapping("/{id}/name")
    public ResponseEntity<?> patchPhaseName(@PathVariable UUID id, @RequestBody PhasePatchNameDto phasePatchNameDto) {
        MDC.put("transactionId", UUID.randomUUID().toString());
        ticketSystemService.patchPhaseName(id, phasePatchNameDto);
        MDC.remove("transactionId");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{id}/position")
    public ResponseEntity<?> patchPhasePosition(@PathVariable UUID id, @RequestBody PhasePatchPositionDto phasePatchPositionDto) {
        MDC.put("transactionId", UUID.randomUUID().toString());
        ticketSystemService.patchPhasePosition(id, phasePatchPositionDto);
        MDC.remove("transactionId");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePhase(@PathVariable UUID id) {
        MDC.put("transactionId", UUID.randomUUID().toString());
        ticketSystemService.deletePhaseById(id);
        MDC.remove("transactionId");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
