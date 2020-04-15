package com.anticoronabrigade.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserExceptionController {
    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<Object> exception (UserNotFoundException exception) {
        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = UserAlreadyRegisteredException.class)
    public ResponseEntity<Object> exception (UserAlreadyRegisteredException exception) {
        return new ResponseEntity<>("User already registered", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = NullValueInNotNullColumnException.class)
    public ResponseEntity<Object> exception (NullValueInNotNullColumnException exception) {
        return new ResponseEntity<>("Null value violates not null constraint", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UnauthorizedException.class)
    public ResponseEntity<Object> exception (UnauthorizedException exception) {
        return new ResponseEntity<>("Unauthorized request", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = CodeUnregisteredException.class)
    public ResponseEntity<Object> exception (CodeUnregisteredException exception) {
        return new ResponseEntity<>("Bad code", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = EmailException.class)
    public ResponseEntity<Object> exception (EmailException exception) {
        return new ResponseEntity<>("Error when trying to send email", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = SmsException.class)
    public ResponseEntity<Object> exception (SmsException exception) {
        return new ResponseEntity<>("Error when trying to send sms", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
