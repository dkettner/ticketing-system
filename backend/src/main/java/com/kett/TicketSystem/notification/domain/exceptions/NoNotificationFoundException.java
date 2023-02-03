package com.kett.TicketSystem.notification.domain.exceptions;

public class NoNotificationFoundException extends RuntimeException {
    public NoNotificationFoundException(String message) {
        super(message);
    }
}
