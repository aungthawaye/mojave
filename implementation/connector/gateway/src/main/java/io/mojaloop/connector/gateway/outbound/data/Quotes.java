package io.mojaloop.connector.gateway.outbound.data;

import io.mojaloop.fspiop.common.type.Payee;
import io.mojaloop.fspiop.spec.core.ErrorInformationObject;
import io.mojaloop.fspiop.spec.core.QuotesIDPutResponse;
import io.mojaloop.fspiop.spec.core.QuotesPostRequest;

public record Quotes() {

    public record Request(Payee payee, QuotesPostRequest request) { }

    public record Response(Payee payee, String quoteId, QuotesIDPutResponse response) { }

    public record Error(Payee payee,
                        String quoteId,
                        ErrorInformationObject errorInformationObject) { }

}
