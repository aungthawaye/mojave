package org.mojave.core.participant.contract.exception.ssp;

import lombok.Getter;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;

import java.util.Map;

@Getter
public class SspEndpointBaseUrlTooLongException extends UncheckedDomainException {

    public static final String CODE = "SSP_ENDPOINT_BASE_URL_TOO_LONG";

    private static final String TEMPLATE =
        "Base URL of SSP Endpoint is too long. Must not exceed " +
            StringSizeConstraints.MAX_HTTP_URL_LENGTH + " characters.";

    public SspEndpointBaseUrlTooLongException() {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[0]));
    }

    public static SspEndpointBaseUrlTooLongException from(final Map<String, String> extras) {

        return new SspEndpointBaseUrlTooLongException();
    }

    @Override
    public Map<String, String> extras() {

        return Map.of();
    }

}