package io.mojaloop.connector.gateway.inbound.command.quotes;

import io.mojaloop.fspiop.common.type.Source;
import io.mojaloop.fspiop.spec.core.QuotesIDPutResponse;

public interface HandleQuotesResponseCommand {

    Output execute(Input input);

    record Input(Source source, String quoteId, QuotesIDPutResponse response) { }

    record Output() { }

}
