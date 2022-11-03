package com.kett.TicketSystem.phase.domain.exceptions;

public class UnrelatedPhaseException extends RuntimeException {
    public UnrelatedPhaseException(String message) {
        super(message);
    }
}
