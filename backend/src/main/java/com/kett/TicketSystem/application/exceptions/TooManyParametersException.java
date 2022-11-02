package com.kett.TicketSystem.application.exceptions;

public class TooManyParametersException extends RuntimeException {
    public TooManyParametersException(String message) {
        super(message);
    }
}
