package io.mojaloop.connector.gateway.inbound.data;

import io.mojaloop.fspiop.spec.core.QuotesIDPutResponse;

public record QuotesResult(String quoteId, QuotesIDPutResponse response) { }