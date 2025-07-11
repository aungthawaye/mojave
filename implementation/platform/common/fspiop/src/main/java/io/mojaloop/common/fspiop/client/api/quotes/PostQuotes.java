package io.mojaloop.common.fspiop.client.api.quotes;

import io.mojaloop.common.fspiop.model.core.QuotesPostRequest;
import io.mojaloop.common.fspiop.support.Destination;

public interface PostQuotes {

    void postQuotes(Destination destination, QuotesPostRequest quotesPostRequest);

}
