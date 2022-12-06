package com.kett.TicketSystem.common.domainprimitives;

public class EmailAddressException extends RuntimeException {
    public EmailAddressException(String message) {
        super(message);
    }
}
