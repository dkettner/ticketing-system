package com.kett.TicketSystem.phase.application;

import com.kett.TicketSystem.common.exceptions.NoParametersException;
import com.kett.TicketSystem.phase.application.dto.PhasePutNameDto;
import com.kett.TicketSystem.phase.application.dto.PhasePutPositionDto;
import com.kett.TicketSystem.phase.application.dto.PhasePostDto;
import com.kett.TicketSystem.phase.application.dto.PhaseResponseDto;
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
@CrossOrigin(origins = {"http://localhost:10000"}, allowCredentials = "true")
@RequestMapping("/phases")
public class PhaseController {
    private final PhaseApplicationService phaseApplicationService;

    @Autowired
    public PhaseController(PhaseApplicationService phaseApplicationService) {
        this.phaseApplicationService = phaseApplicationService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PhaseResponseDto> getPhaseById(@PathVariable UUID id) {
        PhaseResponseDto phaseResponseDto = phaseApplicationService.getPhaseById(id);
        return new ResponseEntity<>(phaseResponseDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<PhaseResponseDto>> getPhasesByQuery(
            @RequestParam(name = "project-id", required = true) UUID projectId
    ) {
        if (projectId == null) { // TODO: null check not needed?
            throw new NoParametersException("cannot query if no projectId is specified");
        }
        List<PhaseResponseDto> phaseResponseDtos = phaseApplicationService.getPhasesByProjectId(projectId);

        return new ResponseEntity<>(phaseResponseDtos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PhaseResponseDto> postPhase(@RequestBody PhasePostDto phasePostDto) {
        PhaseResponseDto phaseResponseDto = phaseApplicationService.addPhase(phasePostDto);
        URI returnURI = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(phaseResponseDto.getId())
                .toUri();

        return ResponseEntity
                .created(returnURI)
                .body(phaseResponseDto);
    }

    @PutMapping("/{id}/name")
    public ResponseEntity<?> patchPhaseName(@PathVariable UUID id, @RequestBody PhasePutNameDto phasePutNameDto) {
        phaseApplicationService.patchPhaseName(id, phasePutNameDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/position")
    public ResponseEntity<?> patchPhasePosition(@PathVariable UUID id, @RequestBody PhasePutPositionDto phasePutPositionDto) {
        phaseApplicationService.patchPhasePosition(id, phasePutPositionDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePhase(@PathVariable UUID id) {
        phaseApplicationService.deletePhaseById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
