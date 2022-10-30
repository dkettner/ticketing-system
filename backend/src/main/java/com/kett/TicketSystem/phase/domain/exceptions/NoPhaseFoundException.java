package com.kett.TicketSystem.phase.domain.exceptions;

public class NoPhaseFoundException extends RuntimeException {
    public NoPhaseFoundException(String message) {
        super(message);
    }
}
