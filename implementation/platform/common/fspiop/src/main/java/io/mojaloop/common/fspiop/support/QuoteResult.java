package io.mojaloop.common.fspiop.support;

import io.mojaloop.common.fspiop.model.core.QuotesIDPutResponse;

public record QuoteResult(String quoteId, QuotesIDPutResponse details) { }
