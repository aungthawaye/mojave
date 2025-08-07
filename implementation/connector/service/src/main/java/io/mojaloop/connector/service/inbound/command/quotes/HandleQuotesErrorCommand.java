package io.mojaloop.connector.service.inbound.command.quotes;

import io.mojaloop.fspiop.common.type.Source;
import io.mojaloop.fspiop.spec.core.ErrorInformationObject;

public interface HandleQuotesErrorCommand {

    Output execute(Input input);

    record Input(Source source, String quoteId, ErrorInformationObject errorInformationObject) { }

    record Output() { }

}
