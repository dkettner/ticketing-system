package com.kett.TicketSystem.project.domain;

public class NoProjectFoundException extends RuntimeException {
    public NoProjectFoundException(String message) {
        super(message);
    }
}
