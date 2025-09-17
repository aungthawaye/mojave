package io.mojaloop.fspiop.service.component;

import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.spec.core.ErrorInformation;
import io.mojaloop.fspiop.spec.core.ErrorInformationObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class FspiopServiceControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorInformationObject> handle(MethodArgumentNotValidException e) {

        return new ResponseEntity<>(
            new ErrorInformationObject().errorInformation(new ErrorInformation(FspiopErrors.MISSING_MANDATORY_ELEMENT.errorType().getCode(), e.getMessage())),
            HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorInformationObject> handle(MethodArgumentTypeMismatchException e) {

        return new ResponseEntity<>(
            new ErrorInformationObject().errorInformation(new ErrorInformation(FspiopErrors.MISSING_MANDATORY_ELEMENT.errorType().getCode(), e.getMessage())),
            HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorInformationObject> handle(NoHandlerFoundException e) {

        return new ResponseEntity<>(
            new ErrorInformationObject().errorInformation(new ErrorInformation(FspiopErrors.UNKNOWN_URI.errorType().getCode(), e.getMessage())),
            HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorInformationObject> handle(HttpMessageNotReadableException e) {

        return new ResponseEntity<>(
            new ErrorInformationObject().errorInformation(new ErrorInformation(FspiopErrors.MISSING_MANDATORY_ELEMENT.errorType().getCode(), e.getMessage())),
            HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorInformationObject> handle(RuntimeException e) {

        if (e.getCause() instanceof FspiopException fspiopException) {

            return new ResponseEntity<>(fspiopException.toErrorObject(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(
            new ErrorInformationObject().errorInformation(
                new ErrorInformation(FspiopErrors.INTERNAL_SERVER_ERROR.errorType().getCode(), e.getMessage())),
            HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FspiopException.class)
    public ResponseEntity<ErrorInformationObject> handle(FspiopException e) {

        return new ResponseEntity<>(e.toErrorObject(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorInformationObject> handle(Exception e) {

        if (e.getCause() instanceof FspiopException fspiopException) {

            return new ResponseEntity<>(fspiopException.toErrorObject(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(
            new ErrorInformationObject().errorInformation(
                new ErrorInformation(FspiopErrors.INTERNAL_SERVER_ERROR.errorType().getCode(), e.getMessage())),
            HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorInformationObject> handle(MissingServletRequestParameterException e) {

        return new ResponseEntity<>(
            new ErrorInformationObject().errorInformation(new ErrorInformation(FspiopErrors.MISSING_MANDATORY_ELEMENT.errorType().getCode(), e.getMessage())),
            HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorInformationObject> handle(BindException e) {

        return new ResponseEntity<>(
            new ErrorInformationObject().errorInformation(new ErrorInformation(FspiopErrors.UNKNOWN_URI.errorType().getCode(), e.getMessage())),
            HttpStatus.NOT_ACCEPTABLE);
    }

}
