/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
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
 * ===
 */
package org.mojave.rail.fspiop.invoker.api.quotes;

import org.mojave.component.retrofit.RetrofitService;
import org.mojave.rail.fspiop.component.exception.FspiopException;
import org.mojave.rail.fspiop.component.participant.ParticipantContext;
import org.mojave.rail.fspiop.component.type.Payer;
import org.mojave.rail.fspiop.component.handy.FspiopHeaders;
import org.mojave.rail.fspiop.component.retrofit.FspiopErrorDecoder;
import org.mojave.rail.fspiop.component.retrofit.FspiopInvocationExceptionResolver;
import org.mojave.rail.fspiop.invoker.api.QuotesService;
import org.mojave.scheme.fspiop.core.ErrorInformationObject;
import org.mojave.scheme.fspiop.core.QuotesIDPutResponse;
import org.springframework.stereotype.Service;
import java.util.Objects;

@Service
class PutQuotesHandler implements PutQuotes {

    private final ParticipantContext participantContext;

    private final QuotesService quotesService;

    private final FspiopErrorDecoder fspiopErrorDecoder;

    public PutQuotesHandler(ParticipantContext participantContext,
                            QuotesService quotesService,
                            FspiopErrorDecoder fspiopErrorDecoder) {

        Objects.requireNonNull(participantContext);
        Objects.requireNonNull(quotesService);
        Objects.requireNonNull(fspiopErrorDecoder);

        this.participantContext = participantContext;
        this.quotesService = quotesService;
        this.fspiopErrorDecoder = fspiopErrorDecoder;
    }

    @Override
    public void putQuotes(Payer payer, String quoteId, QuotesIDPutResponse quotesIDPutResponse)
        throws FspiopException {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Quotes.forResult(
                this.participantContext.fspCode(), payer.fspCode());

            RetrofitService.invoke(
                this.quotesService.putQuotes(fspiopHeaders, quoteId, quotesIDPutResponse),
                this.fspiopErrorDecoder);

        } catch (RetrofitService.InvocationException e) {

            throw FspiopInvocationExceptionResolver.resolve(e);
        }
    }

    @Override
    public void putQuotesError(Payer payer,
                               String quoteId,
                               ErrorInformationObject errorInformationObject)
        throws FspiopException {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Quotes.forResult(
                this.participantContext.fspCode(), payer.fspCode());

            RetrofitService.invoke(
                this.quotesService.putQuotesError(fspiopHeaders, quoteId, errorInformationObject),
                this.fspiopErrorDecoder);

        } catch (RetrofitService.InvocationException e) {

            throw FspiopInvocationExceptionResolver.resolve(e);
        }
    }

}
