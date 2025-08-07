package io.mojaloop.connector.service.inbound.data;

import io.mojaloop.fspiop.spec.core.QuotesIDPutResponse;

public record QuotesResult(String quoteId, QuotesIDPutResponse response) { }