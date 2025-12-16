/*-
 * ================================================================================
 * Mojave
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */

package org.mojave.fspiop.service.component;

import org.mojave.fspiop.component.error.FspiopErrors;
import org.mojave.fspiop.component.exception.FspiopCommunicationException;
import org.mojave.fspiop.component.exception.FspiopException;
import org.mojave.fspiop.spec.core.ErrorInformation;
import org.mojave.fspiop.spec.core.ErrorInformationObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(
        FspiopServiceControllerAdvice.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorInformationObject> handle(MethodArgumentNotValidException e) {

        LOGGER.error("Error: ", e);

        return new ResponseEntity<>(
            new ErrorInformationObject().errorInformation(
                new ErrorInformation(
                    FspiopErrors.MISSING_MANDATORY_ELEMENT.errorType().getCode(),
                    e.getMessage())), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorInformationObject> handle(MethodArgumentTypeMismatchException e) {

        LOGGER.error("Error: ", e);

        return new ResponseEntity<>(
            new ErrorInformationObject().errorInformation(
                new ErrorInformation(
                    FspiopErrors.MISSING_MANDATORY_ELEMENT.errorType().getCode(),
                    e.getMessage())), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorInformationObject> handle(NoHandlerFoundException e) {

        LOGGER.error("Error: ", e);

        return new ResponseEntity<>(
            new ErrorInformationObject().errorInformation(
                new ErrorInformation(
                    FspiopErrors.UNKNOWN_URI.errorType().getCode(),
                    e.getMessage())), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorInformationObject> handle(HttpMessageNotReadableException e) {

        LOGGER.error("Error: ", e);

        return new ResponseEntity<>(
            new ErrorInformationObject().errorInformation(
                new ErrorInformation(
                    FspiopErrors.MISSING_MANDATORY_ELEMENT.errorType().getCode(),
                    e.getMessage())), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorInformationObject> handle(RuntimeException e) {

        LOGGER.error("Error: ", e);

        if (e.getCause() instanceof FspiopException fspiopException) {

            return new ResponseEntity<>(fspiopException.toErrorObject(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(
            new ErrorInformationObject().errorInformation(
                new ErrorInformation(
                    FspiopErrors.INTERNAL_SERVER_ERROR.errorType().getCode(),
                    e.getMessage())), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FspiopCommunicationException.class)
    public ResponseEntity<ErrorInformationObject> handle(FspiopCommunicationException e) {

        LOGGER.error("Error: ", e);

        return new ResponseEntity<>(e.toErrorObject(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(FspiopException.class)
    public ResponseEntity<ErrorInformationObject> handle(FspiopException e) {

        LOGGER.error("Error: ", e);

        return new ResponseEntity<>(e.toErrorObject(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorInformationObject> handle(Exception e) {

        LOGGER.error("Error: ", e);

        if (e.getCause() instanceof FspiopException fspiopException) {

            return new ResponseEntity<>(fspiopException.toErrorObject(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(
            new ErrorInformationObject().errorInformation(
                new ErrorInformation(
                    FspiopErrors.INTERNAL_SERVER_ERROR.errorType().getCode(),
                    e.getMessage())), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorInformationObject> handle(MissingServletRequestParameterException e) {

        LOGGER.error("Error: ", e);

        return new ResponseEntity<>(
            new ErrorInformationObject().errorInformation(
                new ErrorInformation(
                    FspiopErrors.MISSING_MANDATORY_ELEMENT.errorType().getCode(),
                    e.getMessage())), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorInformationObject> handle(BindException e) {

        LOGGER.error("Error: ", e);

        return new ResponseEntity<>(
            new ErrorInformationObject().errorInformation(
                new ErrorInformation(
                    FspiopErrors.UNKNOWN_URI.errorType().getCode(),
                    e.getMessage())), HttpStatus.NOT_ACCEPTABLE);
    }

}
