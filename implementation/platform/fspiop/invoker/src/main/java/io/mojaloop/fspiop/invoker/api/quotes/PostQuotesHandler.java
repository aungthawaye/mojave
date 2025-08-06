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

package io.mojaloop.fspiop.invoker.api.quotes;

import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.fspiop.common.participant.ParticipantContext;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.component.handy.FspiopHeaders;
import io.mojaloop.fspiop.common.type.Destination;
import io.mojaloop.fspiop.component.retrofit.FspiopErrorDecoder;
import io.mojaloop.fspiop.invoker.api.QuotesService;
import io.mojaloop.fspiop.spec.core.QuotesPostRequest;
import org.springframework.stereotype.Service;

@Service
class PostQuotesHandler implements PostQuotes {

    private final ParticipantContext participantContext;

    private final QuotesService quotesService;

    private final FspiopErrorDecoder fspiopErrorDecoder;

    public PostQuotesHandler(ParticipantContext participantContext, QuotesService quotesService, FspiopErrorDecoder fspiopErrorDecoder) {

        assert participantContext != null;
        assert quotesService != null;
        assert fspiopErrorDecoder != null;

        this.participantContext = participantContext;
        this.quotesService = quotesService;
        this.fspiopErrorDecoder = fspiopErrorDecoder;
    }

    @Override
    public void postQuotes(Destination destination, QuotesPostRequest quotesPostRequest) throws FspiopException {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Quotes.forRequest(this.participantContext.fspCode(), destination.destinationFspCode());

            RetrofitService.invoke(this.quotesService.postQuotes(fspiopHeaders, quotesPostRequest), this.fspiopErrorDecoder);

        } catch (RetrofitService.InvocationException e) {

            throw new FspiopException(FspiopErrors.GENERIC_CLIENT_ERROR, e);
        }
    }

}
