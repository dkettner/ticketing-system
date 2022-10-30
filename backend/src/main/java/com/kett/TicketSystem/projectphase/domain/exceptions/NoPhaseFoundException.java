package com.kett.TicketSystem.projectphase.domain.exceptions;

public class NoPhaseFoundException extends RuntimeException {
    public NoPhaseFoundException(String message) {
        super(message);
    }
}
