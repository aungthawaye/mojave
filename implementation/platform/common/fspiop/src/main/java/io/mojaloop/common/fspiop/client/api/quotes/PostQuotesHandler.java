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
import io.mojaloop.common.fspiop.model.core.ErrorInformationResponse;
import io.mojaloop.common.fspiop.model.core.QuotesPostRequest;
import io.mojaloop.common.fspiop.service.QuotingService;
import io.mojaloop.common.fspiop.support.Destination;
import io.mojaloop.common.fspiop.support.ParticipantDetails;
import org.springframework.stereotype.Service;

@Service
class PostQuotesHandler implements PostQuotes {

    private final ParticipantDetails participantDetails;

    private final QuotingService quotingService;

    private final RetrofitService.ErrorDecoder<ErrorInformationResponse> errorDecoder;

    public PostQuotesHandler(ParticipantDetails participantDetails,
                             QuotingService quotingService,
                             RetrofitService.ErrorDecoder<ErrorInformationResponse> errorDecoder) {

        assert participantDetails != null;
        assert quotingService != null;
        assert errorDecoder != null;

        this.participantDetails = participantDetails;
        this.quotingService = quotingService;
        this.errorDecoder = errorDecoder;
    }

    @Override
    public void postQuotes(Destination destination, QuotesPostRequest quotesPostRequest) {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Quotes.forRequest(this.participantDetails.fspCode().getFspCode(),
                                                                       destination.destinationFspCode().getFspCode());

            RetrofitService.invoke(this.quotingService.postQuotes(fspiopHeaders, quotesPostRequest), this.errorDecoder);

        } catch (RetrofitService.InvocationException e) {
            throw new RuntimeException(e);
        }
    }

}
