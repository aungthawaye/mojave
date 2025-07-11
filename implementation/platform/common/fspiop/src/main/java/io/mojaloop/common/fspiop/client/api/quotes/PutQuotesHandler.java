/*-
 * ================================================================================
 * Mojaloop OSS
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */
package io.mojaloop.common.fspiop.client.api.quotes;

import io.mojaloop.common.component.retrofit.RetrofitService;
import io.mojaloop.common.fspiop.component.FspiopHeaders;
import io.mojaloop.common.fspiop.model.core.ErrorInformationObject;
import io.mojaloop.common.fspiop.model.core.ErrorInformationResponse;
import io.mojaloop.common.fspiop.model.core.QuotesIDPutResponse;
import io.mojaloop.common.fspiop.service.QuotingService;
import io.mojaloop.common.fspiop.support.Destination;
import io.mojaloop.common.fspiop.support.ParticipantSettings;
import org.springframework.stereotype.Service;

@Service
class PutQuotesHandler implements PutQuotes {

    private final ParticipantSettings participantSettings;

    private final QuotingService quotingService;

    private final RetrofitService.ErrorDecoder<ErrorInformationResponse> errorDecoder;

    public PutQuotesHandler(ParticipantSettings participantSettings,
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
    public void putQuotes(Destination destination, String quoteId, QuotesIDPutResponse quotesIDPutResponse) {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Quotes.forCallback(this.participantSettings.fspId(), destination.destinationFspId());

            RetrofitService.invoke(this.quotingService.putQuotes(fspiopHeaders, quoteId, quotesIDPutResponse), this.errorDecoder);

        } catch (RetrofitService.InvocationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void putQuotesError(Destination destination, String quoteId, ErrorInformationObject errorInformationObject) {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Quotes.forCallback(this.participantSettings.fspId(), destination.destinationFspId());

            RetrofitService.invoke(this.quotingService.putQuotesError(fspiopHeaders, quoteId, errorInformationObject), this.errorDecoder);

        } catch (RetrofitService.InvocationException e) {
            throw new RuntimeException(e);
        }
    }

}
