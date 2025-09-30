package io.mojaloop.core.quoting.contract.command;

import io.mojaloop.core.common.datatype.identifier.quoting.UdfQuoteId;
import io.mojaloop.fspiop.service.component.FspiopHttpRequest;

public interface GetQuotesCommand {

    Output execute(Input input);

    record Input(FspiopHttpRequest request, UdfQuoteId udfQuoteId) { }

    record Output() { }

}
