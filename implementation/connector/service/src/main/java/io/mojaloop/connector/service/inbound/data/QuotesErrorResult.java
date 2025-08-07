package io.mojaloop.connector.service.inbound.data;

import io.mojaloop.fspiop.spec.core.ErrorInformationObject;

public record QuotesErrorResult(String quoteId, ErrorInformationObject errorInformation) { }