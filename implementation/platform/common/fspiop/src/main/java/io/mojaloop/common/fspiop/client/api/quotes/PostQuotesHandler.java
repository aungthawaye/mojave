package io.mojaloop.common.fspiop.client.api.quotes;

import io.mojaloop.common.component.retrofit.RetrofitService;
import io.mojaloop.common.fspiop.component.FspiopHeaders;
import io.mojaloop.common.fspiop.model.core.ErrorInformationResponse;
import io.mojaloop.common.fspiop.model.core.QuotesPostRequest;
import io.mojaloop.common.fspiop.service.QuotingService;
import io.mojaloop.common.fspiop.support.Destination;
import io.mojaloop.common.fspiop.support.ParticipantSettings;
import org.springframework.stereotype.Service;

@Service
class PostQuotesHandler implements PostQuotes {

    private final ParticipantSettings participantSettings;

    private final QuotingService quotingService;

    private final RetrofitService.ErrorDecoder<ErrorInformationResponse> errorDecoder;

    public PostQuotesHandler(ParticipantSettings participantSettings,
                             QuotingService quotingService,
                             RetrofitService.ErrorDecoder<ErrorInformationResponse> errorDecoder) {

        assert participantSettings != null;
        assert quotingService != null;
        assert errorDecoder != null;

        this.participantSettings = participantSettings;
        this.quotingService = quotingService;
        this.errorDecoder = errorDecoder;
    }

    @Override
    public void postQuotes(Destination destination, QuotesPostRequest quotesPostRequest) {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Quotes.forRequest(this.participantSettings.fspId(), destination.destinationFspId());

            RetrofitService.invoke(this.quotingService.postQuotes(fspiopHeaders, quotesPostRequest), this.errorDecoder);

        } catch (RetrofitService.InvocationException e) {
            throw new RuntimeException(e);
        }
    }

}
