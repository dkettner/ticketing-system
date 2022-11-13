package com.kett.TicketSystem.user.domain.exceptions;

public class EmailAlreadyInUseException extends RuntimeException {
    public EmailAlreadyInUseException(String message) {
        super(message);
    }
}
