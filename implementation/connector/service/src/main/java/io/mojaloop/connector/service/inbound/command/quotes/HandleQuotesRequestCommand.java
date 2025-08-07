package io.mojaloop.connector.service.inbound.command.quotes;

import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Source;
import io.mojaloop.fspiop.spec.core.QuotesPostRequest;

public interface HandleQuotesRequestCommand {

    void execute(Input input) throws FspiopException;

    record Input(Source source, String quoteId, QuotesPostRequest request) { }

}
