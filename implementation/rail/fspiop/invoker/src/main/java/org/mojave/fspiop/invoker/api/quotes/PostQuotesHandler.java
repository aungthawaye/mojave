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
package org.mojave.fspiop.invoker.api.quotes;

import org.mojave.component.retrofit.RetrofitService;
import org.mojave.fspiop.component.exception.FspiopException;
import org.mojave.fspiop.component.participant.ParticipantContext;
import org.mojave.fspiop.component.type.Payee;
import org.mojave.fspiop.component.handy.FspiopHeaders;
import org.mojave.fspiop.component.retrofit.FspiopErrorDecoder;
import org.mojave.fspiop.component.retrofit.FspiopInvocationExceptionResolver;
import org.mojave.fspiop.invoker.api.QuotesService;
import org.mojave.rail.fspiop.spec.core.QuotesPostRequest;
import org.springframework.stereotype.Service;

@Service
class PostQuotesHandler implements PostQuotes {

    private final ParticipantContext participantContext;

    private final QuotesService quotesService;

    private final FspiopErrorDecoder fspiopErrorDecoder;

    public PostQuotesHandler(ParticipantContext participantContext,
                             QuotesService quotesService,
                             FspiopErrorDecoder fspiopErrorDecoder) {

        assert participantContext != null;
        assert quotesService != null;
        assert fspiopErrorDecoder != null;

        this.participantContext = participantContext;
        this.quotesService = quotesService;
        this.fspiopErrorDecoder = fspiopErrorDecoder;
    }

    @Override
    public void postQuotes(Payee payee, QuotesPostRequest quotesPostRequest)
        throws FspiopException {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Quotes.forRequest(
                this.participantContext.fspCode(), payee.fspCode());

            RetrofitService.invoke(
                this.quotesService.postQuotes(fspiopHeaders, quotesPostRequest),
                this.fspiopErrorDecoder);

        } catch (RetrofitService.InvocationException e) {

            throw FspiopInvocationExceptionResolver.resolve(e);
        }
    }

}
