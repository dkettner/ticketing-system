package com.kett.TicketSystem.phase.application;

import com.kett.TicketSystem.phase.domain.Phase;
import com.kett.TicketSystem.phase.domain.exceptions.NoPhaseFoundException;
import com.kett.TicketSystem.phase.repository.PhaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PhaseService {
    private final PhaseRepository phaseRepository;

    @Autowired
    public PhaseService(PhaseRepository phaseRepository) {
        this.phaseRepository = phaseRepository;
    }

    public Phase getPhaseById(UUID id) {
        return phaseRepository
                .findById(id)
                .orElseThrow(() -> new NoPhaseFoundException("could not find phase with id: " + id));
    }
}
