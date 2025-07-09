package io.mojaloop.common.fspiop.support;

import io.mojaloop.common.fspiop.core.model.QuotesIDPutResponse;

public record QuoteResult(String quoteId, QuotesIDPutResponse details) { }
