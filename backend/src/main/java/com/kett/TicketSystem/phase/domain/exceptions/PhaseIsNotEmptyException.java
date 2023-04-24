package com.kett.TicketSystem.phase.domain.exceptions;

public class PhaseIsNotEmptyException extends RuntimeException {
    public PhaseIsNotEmptyException(String message) {
        super(message);
    }
}
