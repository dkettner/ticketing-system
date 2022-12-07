package com.kett.TicketSystem.phase.domain.exceptions;

public class NoPhaseInProjectException extends RuntimeException {
    public NoPhaseInProjectException(String message) {
        super(message);
    }
}
