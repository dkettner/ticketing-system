package com.kett.TicketSystem.application;

import com.kett.TicketSystem.application.exceptions.ImpossibleException;
import com.kett.TicketSystem.project.domain.exceptions.NoProjectFoundException;
import com.kett.TicketSystem.project.domain.exceptions.ProjectException;
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

    @ExceptionHandler(NoProjectFoundException.class)
    public ResponseEntity<String> handleNoProjectFoundException(NoProjectFoundException noProjectFoundException) {
        return new ResponseEntity<>(noProjectFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ImpossibleException.class)
    public ResponseEntity<String> handleImpossibleException(ImpossibleException impossibleException) {
        return new ResponseEntity<>(impossibleException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
