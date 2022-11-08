package com.kett.TicketSystem.application;

import com.kett.TicketSystem.application.exceptions.ImpossibleException;
import com.kett.TicketSystem.application.exceptions.NoParametersException;
import com.kett.TicketSystem.application.exceptions.TooManyParametersException;
import com.kett.TicketSystem.domainprimitives.EmailAddressException;
import com.kett.TicketSystem.membership.domain.exceptions.IllegalStateUpdateException;
import com.kett.TicketSystem.membership.domain.exceptions.MembershipAlreadyExistsException;
import com.kett.TicketSystem.membership.domain.exceptions.MembershipException;
import com.kett.TicketSystem.membership.domain.exceptions.NoMembershipFoundException;
import com.kett.TicketSystem.project.domain.exceptions.NoProjectFoundException;
import com.kett.TicketSystem.project.domain.exceptions.ProjectException;
import com.kett.TicketSystem.user.domain.exceptions.NoUserFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerManager extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ProjectException.class)
    public ResponseEntity<String> handleProjectException(ProjectException projectException) {
        return new ResponseEntity<>(projectException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoParametersException.class)
    public ResponseEntity<String> handleNoParametersException(NoParametersException noParametersException) {
        return new ResponseEntity<>(noParametersException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TooManyParametersException.class)
    public ResponseEntity<String> handleTooManyParametersException(TooManyParametersException tooManyParametersException) {
        return new ResponseEntity<>(tooManyParametersException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MembershipException.class)
    public ResponseEntity<String> handleMembershipException(MembershipException membershipException) {
        return new ResponseEntity<>(membershipException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailAddressException.class)
    public ResponseEntity<String> handleEMailAddressException(EmailAddressException eMailAddressException) {
        return new ResponseEntity<>(eMailAddressException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoProjectFoundException.class)
    public ResponseEntity<String> handleNoProjectFoundException(NoProjectFoundException noProjectFoundException) {
        return new ResponseEntity<>(noProjectFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoMembershipFoundException.class)
    public ResponseEntity<String> handleNoMembershipFoundException(NoMembershipFoundException noMembershipFoundException) {
        return new ResponseEntity<>(noMembershipFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoUserFoundException.class)
    public ResponseEntity<String> handleTicketException(NoUserFoundException noUserFoundException) {
        return new ResponseEntity<>(noUserFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MembershipAlreadyExistsException.class)
    public ResponseEntity<String> handleMembershipAlreadyExistsException(MembershipAlreadyExistsException membershipAlreadyExistsException) {
        return new ResponseEntity<>(membershipAlreadyExistsException.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalStateUpdateException.class)
    public ResponseEntity<String> handleIllegalStateUpdateException(IllegalStateUpdateException illegalStateUpdateException) {
        return new ResponseEntity<>(illegalStateUpdateException.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ImpossibleException.class)
    public ResponseEntity<String> handleImpossibleException(ImpossibleException impossibleException) {
        return new ResponseEntity<>(impossibleException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
