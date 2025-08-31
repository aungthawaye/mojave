package io.mojaloop.core.participant.admin.client.exception;

import lombok.Getter;

public class ParticipantCommandClientException extends Exception {

    @Getter
    private final String code;

    public ParticipantCommandClientException(String code, String message) {

        super(message);
        this.code = code;
    }

}
