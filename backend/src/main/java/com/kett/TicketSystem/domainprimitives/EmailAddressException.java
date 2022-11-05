package com.kett.TicketSystem.domainprimitives;

public class EmailAddressException extends RuntimeException {
    public EmailAddressException(String message) {
        super(message);
    }
}
