package io.mojaloop.common.fspiop.client.api.quotes;

import io.mojaloop.common.fspiop.core.model.ErrorInformationObject;
import io.mojaloop.common.fspiop.core.model.QuotesIDPutResponse;
import io.mojaloop.common.fspiop.support.Destination;
import retrofit2.http.Body;
import retrofit2.http.Path;

public interface PutQuotes {

    void putQuotes(Destination destination, @Path("quoteId") String quoteId, @Body QuotesIDPutResponse quotesIDPutResponse);

    void putQuotesError(Destination destination, @Path("quoteId") String quoteId, @Body ErrorInformationObject errorInformationObject);

}
