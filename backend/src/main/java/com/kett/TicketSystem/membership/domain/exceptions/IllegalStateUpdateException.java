package com.kett.TicketSystem.membership.domain.exceptions;

public class IllegalStateUpdateException extends RuntimeException {
    public IllegalStateUpdateException(String message) {
        super(message);
    }
}
