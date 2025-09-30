package io.mojaloop.core.quoting.contract.command;

import io.mojaloop.core.common.datatype.identifier.quoting.UdfQuoteId;
import io.mojaloop.fspiop.service.component.FspiopHttpRequest;
import io.mojaloop.fspiop.spec.core.ErrorInformationObject;

public interface PutQuotesErrorCommand {

    Output execute(Input input);

    record Input(FspiopHttpRequest request, UdfQuoteId udfQuoteId, ErrorInformationObject error) { }

    record Output() { }

}
