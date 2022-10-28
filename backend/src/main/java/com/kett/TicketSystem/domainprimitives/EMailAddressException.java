package com.kett.TicketSystem.domainprimitives;

public class EMailAddressException extends RuntimeException {
    public EMailAddressException(String message) {
        super(message);
    }
}
