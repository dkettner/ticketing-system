package com.kett.TicketSystem.phase.application;

import com.kett.TicketSystem.common.DtoMapper;
import com.kett.TicketSystem.phase.application.dto.PhasePutNameDto;
import com.kett.TicketSystem.phase.application.dto.PhasePutPositionDto;
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
    public void patchPhaseName(UUID id, PhasePutNameDto phasePutNameDto) {
        phaseDomainService.patchPhaseName(id, phasePutNameDto.getName());
    }

    @PreAuthorize("hasAuthority('ROLE_PROJECT_ADMIN_'.concat(@phaseDomainService.getProjectIdByPhaseId(#id)))")
    public void patchPhasePosition(UUID id, PhasePutPositionDto phasePutPositionDto) {
        phaseDomainService.patchPhasePosition(id, phasePutPositionDto.getPreviousPhase());
    }

    @PreAuthorize("hasAuthority('ROLE_PROJECT_ADMIN_'.concat(@phaseDomainService.getProjectIdByPhaseId(#id)))")
    public void deletePhaseById(UUID id) {
        phaseDomainService.deleteById(id);
    }
}
