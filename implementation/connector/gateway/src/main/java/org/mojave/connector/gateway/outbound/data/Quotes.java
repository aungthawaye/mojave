package org.mojave.connector.gateway.outbound.data;

import org.mojave.fspiop.common.type.Payee;
import org.mojave.fspiop.spec.core.ErrorInformationObject;
import org.mojave.fspiop.spec.core.QuotesIDPutResponse;
import org.mojave.fspiop.spec.core.QuotesPostRequest;

public record Quotes() {

    public record Request(Payee payee, QuotesPostRequest request) { }

    public record Response(Payee payee, String quoteId, QuotesIDPutResponse response) { }

    public record Error(Payee payee,
                        String quoteId,
                        ErrorInformationObject errorInformationObject) { }

}
