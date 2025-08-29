package io.mojaloop.core.participant.intercom.client.exception;

import lombok.Getter;

public class ParticipantIntercomClientException extends Exception {

    @Getter
    private final String code;

    public ParticipantIntercomClientException(String code, String message) {

        super(message);
        this.code = code;
    }

}
