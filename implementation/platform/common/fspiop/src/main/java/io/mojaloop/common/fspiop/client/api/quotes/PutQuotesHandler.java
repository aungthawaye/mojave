package io.mojaloop.common.fspiop.client.api.quotes;

import io.mojaloop.common.component.retrofit.RetrofitService;
import io.mojaloop.common.fspiop.component.FspiopHeaders;
import io.mojaloop.common.fspiop.core.model.ErrorInformationObject;
import io.mojaloop.common.fspiop.core.model.ErrorInformationResponse;
import io.mojaloop.common.fspiop.core.model.QuotesIDPutResponse;
import io.mojaloop.common.fspiop.service.QuotingService;
import io.mojaloop.common.fspiop.support.Destination;
import io.mojaloop.common.fspiop.support.FspSettings;
import org.springframework.stereotype.Service;

@Service
class PutQuotesHandler implements PutQuotes {

    private final FspSettings fspSettings;

    private final QuotingService quotingService;

    private final RetrofitService.ErrorDecoder<ErrorInformationResponse> errorDecoder;

    public PutQuotesHandler(FspSettings fspSettings,
                            QuotingService quotingService,
                            RetrofitService.ErrorDecoder<ErrorInformationResponse> errorDecoder) {

        assert fspSettings != null;
        assert quotingService != null;
        assert errorDecoder != null;

        this.fspSettings = fspSettings;
        this.quotingService = quotingService;
        this.errorDecoder = errorDecoder;
    }

    @Override
    public void putQuotes(Destination destination, String quoteId, QuotesIDPutResponse quotesIDPutResponse) {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Quotes.forCallback(this.fspSettings.fspId(), destination.destinationFspId());

            RetrofitService.invoke(this.quotingService.putQuotes(fspiopHeaders, quoteId, quotesIDPutResponse), this.errorDecoder);

        } catch (RetrofitService.InvocationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void putQuotesError(Destination destination, String quoteId, ErrorInformationObject errorInformationObject) {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Quotes.forCallback(this.fspSettings.fspId(), destination.destinationFspId());

            RetrofitService.invoke(this.quotingService.putQuotesError(fspiopHeaders, quoteId, errorInformationObject), this.errorDecoder);

        } catch (RetrofitService.InvocationException e) {
            throw new RuntimeException(e);
        }
    }

}
