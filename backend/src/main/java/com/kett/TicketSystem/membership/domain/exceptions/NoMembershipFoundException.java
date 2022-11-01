package com.kett.TicketSystem.membership.domain.exceptions;

public class NoMembershipFoundException extends RuntimeException {
    public NoMembershipFoundException(String message) {
        super(message);
    }
}
