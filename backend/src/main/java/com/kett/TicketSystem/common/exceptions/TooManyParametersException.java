package com.kett.TicketSystem.common.exceptions;

public class TooManyParametersException extends RuntimeException {
    public TooManyParametersException(String message) {
        super(message);
    }
}
