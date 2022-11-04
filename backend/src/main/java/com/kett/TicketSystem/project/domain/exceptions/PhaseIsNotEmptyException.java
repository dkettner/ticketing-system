package com.kett.TicketSystem.project.domain.exceptions;

public class PhaseIsNotEmptyException extends RuntimeException {
    public PhaseIsNotEmptyException(String message) {
        super(message);
    }
}
