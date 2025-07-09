package io.mojaloop.common.fspiop.client.api.quotes;

import io.mojaloop.common.component.retrofit.RetrofitService;
import io.mojaloop.common.fspiop.component.FspiopHeaders;
import io.mojaloop.common.fspiop.core.model.ErrorInformationResponse;
import io.mojaloop.common.fspiop.core.model.QuotesPostRequest;
import io.mojaloop.common.fspiop.service.QuotingService;
import io.mojaloop.common.fspiop.support.Destination;
import io.mojaloop.common.fspiop.support.FspSettings;
import org.springframework.stereotype.Service;

@Service
class PostQuotesHandler implements PostQuotes {

    private final FspSettings fspSettings;

    private final QuotingService quotingService;

    private final RetrofitService.ErrorDecoder<ErrorInformationResponse> errorDecoder;

    public PostQuotesHandler(FspSettings fspSettings,
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
    public void postQuotes(Destination destination, QuotesPostRequest quotesPostRequest) {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Quotes.forRequest(this.fspSettings.fspId(), destination.destinationFspId());

            RetrofitService.invoke(this.quotingService.postQuotes(fspiopHeaders, quotesPostRequest), this.errorDecoder);

        } catch (RetrofitService.InvocationException e) {
            throw new RuntimeException(e);
        }
    }

}
