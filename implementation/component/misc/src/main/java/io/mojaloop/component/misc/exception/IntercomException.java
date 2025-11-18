package io.mojaloop.component.misc.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class IntercomException extends Exception {

    private final String code;

    private final Map<String, String> extras;

    public IntercomException(String code, String message, Map<String, String> extras) {

        super(message);

        this.code = code;
        this.extras = extras;
    }

    public IntercomException(String code, String message) {

        this(code, message, null);
    }

}
