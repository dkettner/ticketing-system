package com.kett.TicketSystem.membership.domain.exceptions;

public class AlreadyLastAdminException extends RuntimeException {
    public AlreadyLastAdminException(String message) {
        super(message);
    }
}
