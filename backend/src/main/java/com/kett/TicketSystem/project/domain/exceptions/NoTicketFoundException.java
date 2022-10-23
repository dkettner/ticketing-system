package com.kett.TicketSystem.project.domain.exceptions;

public class NoTicketFoundException extends RuntimeException {
    public NoTicketFoundException(String message) {
        super(message);
    }
}
