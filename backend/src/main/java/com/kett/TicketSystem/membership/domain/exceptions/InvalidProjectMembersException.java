package com.kett.TicketSystem.membership.domain.exceptions;

public class InvalidProjectMembersException extends RuntimeException {
    public InvalidProjectMembersException(String message) {
        super(message);
    }
}
