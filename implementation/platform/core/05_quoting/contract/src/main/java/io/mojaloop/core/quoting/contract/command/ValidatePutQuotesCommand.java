package io.mojaloop.core.quoting.contract.command;

import io.mojaloop.core.common.datatype.identifier.quoting.UdfQuoteId;
import io.mojaloop.fspiop.service.component.FspiopHttpRequest;
import io.mojaloop.fspiop.spec.core.QuotesIDPutResponse;

public interface ValidatePutQuotesCommand {

    Output execute(Input input);

    record Input(FspiopHttpRequest request, UdfQuoteId udfQuoteId, QuotesIDPutResponse quotesIDPutResponse) { }

    record Output() { }

}
