package com.kett.TicketSystem.common.exceptions;

import com.kett.TicketSystem.common.domainprimitives.EmailAddressException;
import com.kett.TicketSystem.membership.domain.exceptions.*;
import com.kett.TicketSystem.notification.domain.exceptions.NoNotificationFoundException;
import com.kett.TicketSystem.notification.domain.exceptions.NotificationException;
import com.kett.TicketSystem.phase.domain.exceptions.LastPhaseException;
import com.kett.TicketSystem.phase.domain.exceptions.NoPhaseFoundException;
import com.kett.TicketSystem.phase.domain.exceptions.NoPhaseInProjectException;
import com.kett.TicketSystem.phase.domain.exceptions.PhaseException;
import com.kett.TicketSystem.phase.domain.exceptions.PhaseIsNotEmptyException;
import com.kett.TicketSystem.project.domain.exceptions.ProjectException;
import com.kett.TicketSystem.ticket.domain.exceptions.NoTicketFoundException;
import com.kett.TicketSystem.ticket.domain.exceptions.TicketException;
import com.kett.TicketSystem.user.domain.exceptions.EmailAlreadyInUseException;
import com.kett.TicketSystem.user.domain.exceptions.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerManager extends ResponseEntityExceptionHandler {
    Logger logger = LoggerFactory.getLogger(ExceptionHandlerManager.class);

    @ExceptionHandler(value = {
            NoParametersException.class,
            TooManyParametersException.class,
            MembershipException.class,
            NotificationException.class,
            PhaseException.class,
            ProjectException.class,
            TicketException.class,
            UserException.class,
            EmailAddressException.class
    })
    public ResponseEntity<String> handleBadRequestException(RuntimeException runtimeException) {
        logger.warn("exception -> " + runtimeException.getClass().getSimpleName() + ": " + runtimeException.getMessage());
        return new ResponseEntity<>(runtimeException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {
            NoMembershipFoundException.class,
            NoNotificationFoundException.class,
            NoPhaseFoundException.class,
            NoProjectFoundException.class,
            NoTicketFoundException.class,
            NoUserFoundException.class
    })
    public ResponseEntity<String> handleNotFoundException(RuntimeException runtimeException) {
        logger.warn("exception -> " + runtimeException.getClass().getSimpleName() + ": " + runtimeException.getMessage());
        return new ResponseEntity<>(runtimeException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {
            MembershipAlreadyExistsException.class,
            AlreadyLastAdminException.class,
            IllegalStateUpdateException.class,
            PhaseIsNotEmptyException.class,
            NoPhaseInProjectException.class,
            LastPhaseException.class,
            InvalidProjectMembersException.class,
            EmailAlreadyInUseException.class,
            UnrelatedPhaseException.class
    })
    public ResponseEntity<String> handleConflictException(RuntimeException runtimeException) {
        logger.warn("exception -> " + runtimeException.getClass().getSimpleName() + ": " + runtimeException.getMessage());
        return new ResponseEntity<>(runtimeException.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {
            ImpossibleException.class
    })
    public ResponseEntity<String> handleInternalErrorServerException(ImpossibleException impossibleException) {
        logger.error("exception -> " + impossibleException.getClass().getSimpleName() + ": " + impossibleException.getMessage());
        return new ResponseEntity<>(impossibleException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
