package com.kett.TicketSystem.ticket.domain.exceptions;

public class NoTicketFoundException extends RuntimeException {
    public NoTicketFoundException(String message) {
        super(message);
    }
}
