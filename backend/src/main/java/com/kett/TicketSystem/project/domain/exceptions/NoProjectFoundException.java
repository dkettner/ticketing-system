package com.kett.TicketSystem.project.domain.exceptions;

public class NoProjectFoundException extends RuntimeException {
    public NoProjectFoundException(String message) {
        super(message);
    }
}
