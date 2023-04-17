package com.kett.TicketSystem.phase.application;

import com.kett.TicketSystem.application.DtoMapper;
import com.kett.TicketSystem.phase.application.dto.PhasePatchNameDto;
import com.kett.TicketSystem.phase.application.dto.PhasePatchPositionDto;
import com.kett.TicketSystem.phase.application.dto.PhasePostDto;
import com.kett.TicketSystem.phase.application.dto.PhaseResponseDto;
import com.kett.TicketSystem.phase.domain.Phase;
import com.kett.TicketSystem.phase.domain.PhaseDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PhaseApplicationService {
    private final PhaseDomainService phaseDomainService;
    private final DtoMapper dtoMapper;

    @Autowired
    public PhaseApplicationService(PhaseDomainService phaseDomainService, DtoMapper dtoMapper) {
        this.phaseDomainService = phaseDomainService;
        this.dtoMapper = dtoMapper;
    }

    @PreAuthorize("hasAnyAuthority(" +
            "'ROLE_PROJECT_ADMIN_'.concat(@phaseDomainService.getProjectIdByPhaseId(#id)), " +
            "'ROLE_PROJECT_MEMBER_'.concat(@phaseDomainService.getProjectIdByPhaseId(#id)))")
    public PhaseResponseDto getPhaseById(UUID id) {
        Phase phase = phaseDomainService.getPhaseById(id);
        return dtoMapper.mapPhaseToPhaseResponseDto(phase);
    }

    @PreAuthorize("hasAnyAuthority(" +
            "'ROLE_PROJECT_ADMIN_'.concat(#projectId), " +
            "'ROLE_PROJECT_MEMBER_'.concat(#projectId))")
    public List<PhaseResponseDto> getPhasesByProjectId(UUID projectId) {
        List<Phase> phases = phaseDomainService.getPhasesByProjectId(projectId);
        return dtoMapper.mapPhaseListToPhaseResponseDtoList(phases);
    }

    @PreAuthorize("hasAuthority('ROLE_PROJECT_ADMIN_'.concat(#phasePostDto.projectId))")
    public PhaseResponseDto addPhase(PhasePostDto phasePostDto) {
        Phase phase = phaseDomainService.createPhase(
                dtoMapper.mapPhasePostDtoToPhase(phasePostDto), phasePostDto.getPreviousPhaseId()
        );
        return dtoMapper.mapPhaseToPhaseResponseDto(phase);
    }


    @PreAuthorize("hasAuthority('ROLE_PROJECT_ADMIN_'.concat(@phaseDomainService.getProjectIdByPhaseId(#id)))")
    public void patchPhaseName(UUID id, PhasePatchNameDto phasePatchNameDto) {
        phaseDomainService.patchPhaseName(id, phasePatchNameDto.getName());
    }

    @PreAuthorize("hasAuthority('ROLE_PROJECT_ADMIN_'.concat(@phaseDomainService.getProjectIdByPhaseId(#id)))")
    public void patchPhasePosition(UUID id, PhasePatchPositionDto phasePatchPositionDto) {
        phaseDomainService.patchPhasePosition(id, phasePatchPositionDto.getPreviousPhase());
    }

    @PreAuthorize("hasAuthority('ROLE_PROJECT_ADMIN_'.concat(@phaseDomainService.getProjectIdByPhaseId(#id)))")
    public void deletePhaseById(UUID id) {
        phaseDomainService.deleteById(id);
    }
}
