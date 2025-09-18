/*-
 * ================================================================================
 * Mojaloop OSS
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
package io.mojaloop.component.web.error;

import io.mojaloop.component.misc.error.RestErrorResponse;
import io.mojaloop.component.misc.exception.DomainException;
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
public class RestErrorControllerAdvice {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<RestErrorResponse> handle(DomainException e) {

        return new ResponseEntity<>(new RestErrorResponse(e.getTemplate().code(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestErrorResponse> handle(MethodArgumentNotValidException e) {

        return new ResponseEntity<>(new RestErrorResponse("ARGUMENT_NOT_VALID", e.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<RestErrorResponse> handle(MethodArgumentTypeMismatchException e) {

        return new ResponseEntity<>(new RestErrorResponse("ARGUMENT_TYPE_MISMATCH", e.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<RestErrorResponse> handle(NoHandlerFoundException e) {

        return new ResponseEntity<>(new RestErrorResponse("NO_HANDLER_FOUND", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RestErrorResponse> handle(HttpMessageNotReadableException e) {

        return new ResponseEntity<>(new RestErrorResponse("MESSAGE_NOT_READABLE", e.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<RestErrorResponse> handle(RuntimeException e) {

        return new ResponseEntity<>(new RestErrorResponse("INTERNAL_SERVER_ERROR", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestErrorResponse> handle(Exception e) {

        return new ResponseEntity<>(new RestErrorResponse("INTERNAL_SERVER_ERROR", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<RestErrorResponse> handle(MissingServletRequestParameterException e) {

        return new ResponseEntity<>(new RestErrorResponse("MISSING_PARAMETER", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<RestErrorResponse> handle(BindException e) {

        return new ResponseEntity<>(new RestErrorResponse("BINDING_FAILED", e.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
    }

}
