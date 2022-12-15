package com.kett.TicketSystem.common.exceptions;

import com.kett.TicketSystem.common.domainprimitives.EmailAddressException;
import com.kett.TicketSystem.membership.domain.exceptions.*;
import com.kett.TicketSystem.phase.domain.exceptions.LastPhaseException;
import com.kett.TicketSystem.phase.domain.exceptions.NoPhaseFoundException;
import com.kett.TicketSystem.phase.domain.exceptions.NoPhaseInProjectException;
import com.kett.TicketSystem.phase.domain.exceptions.PhaseException;
import com.kett.TicketSystem.project.domain.exceptions.PhaseIsNotEmptyException;
import com.kett.TicketSystem.project.domain.exceptions.ProjectException;
import com.kett.TicketSystem.ticket.domain.exceptions.NoTicketFoundException;
import com.kett.TicketSystem.ticket.domain.exceptions.TicketException;
import com.kett.TicketSystem.user.domain.exceptions.NoUserFoundException;
import com.kett.TicketSystem.user.domain.exceptions.EmailAlreadyInUseException;
import com.kett.TicketSystem.user.domain.exceptions.UserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerManager extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {
            NoParametersException.class,
            TooManyParametersException.class,
            MembershipException.class,
            PhaseException.class,
            ProjectException.class,
            TicketException.class,
            UserException.class,
            EmailAddressException.class
    })
    public ResponseEntity<String> handleBadRequestException(RuntimeException runtimeException) {
        return new ResponseEntity<>(runtimeException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {
            NoMembershipFoundException.class,
            NoPhaseFoundException.class,
            NoProjectFoundException.class,
            NoTicketFoundException.class,
            NoUserFoundException.class
    })
    public ResponseEntity<String> handleNotFoundException(RuntimeException runtimeException) {
        return new ResponseEntity<>(runtimeException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {
            MembershipAlreadyExistsException.class,
            IllegalStateUpdateException.class,
            PhaseIsNotEmptyException.class,
            NoPhaseInProjectException.class,
            LastPhaseException.class,
            InvalidProjectMembersException.class,
            EmailAlreadyInUseException.class
    })
    public ResponseEntity<String> handleConflictException(RuntimeException runtimeException) {
        return new ResponseEntity<>(runtimeException.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {
            ImpossibleException.class
    })
    public ResponseEntity<String> handleInternalErrorServerException(ImpossibleException impossibleException) {
        return new ResponseEntity<>(impossibleException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
