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
package org.mojave.rail.fspiop.bootstrap.api.quotes;

import org.mojave.component.retrofit.RetrofitService;
import org.mojave.rail.fspiop.component.exception.FspiopException;
import org.mojave.rail.fspiop.component.participant.ParticipantContext;
import org.mojave.rail.fspiop.component.type.Payer;
import org.mojave.rail.fspiop.component.handy.FspiopHeaders;
import org.mojave.rail.fspiop.component.retrofit.FspiopErrorDecoder;
import org.mojave.rail.fspiop.component.retrofit.FspiopInvocationExceptionResolver;
import org.mojave.rail.fspiop.bootstrap.api.QuotesResponseService;
import org.mojave.scheme.fspiop.core.ErrorInformationObject;
import org.mojave.scheme.fspiop.core.QuotesIDPutResponse;
import org.springframework.stereotype.Service;

@Service
public class RespondQuotesHandler implements RespondQuotes {

    private final ParticipantContext participantContext;

    private final QuotesResponseService quotesResponseService;

    private final FspiopErrorDecoder fspiopErrorDecoder;

    public RespondQuotesHandler(ParticipantContext participantContext,
                                QuotesResponseService quotesResponseService,
                                FspiopErrorDecoder fspiopErrorDecoder) {

        assert participantContext != null;
        assert quotesResponseService != null;
        assert fspiopErrorDecoder != null;

        this.participantContext = participantContext;
        this.quotesResponseService = quotesResponseService;
        this.fspiopErrorDecoder = fspiopErrorDecoder;

    }

    @Override
    public void putQuotes(Payer payer, String url, QuotesIDPutResponse response)
        throws FspiopException {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Quotes.forResult(
                this.participantContext.fspCode(), payer.fspCode());

            RetrofitService.invoke(
                this.quotesResponseService.putQuotes(url, fspiopHeaders, response),
                this.fspiopErrorDecoder);

        } catch (RetrofitService.InvocationException e) {

            throw FspiopInvocationExceptionResolver.resolve(e);
        }
    }

    @Override
    public void putQuotesError(Payer payer, String url, ErrorInformationObject error)
        throws FspiopException {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Quotes.forResult(
                this.participantContext.fspCode(), payer.fspCode());

            RetrofitService.invoke(
                this.quotesResponseService.putQuotesError(url, fspiopHeaders, error),
                this.fspiopErrorDecoder);

        } catch (RetrofitService.InvocationException e) {

            throw FspiopInvocationExceptionResolver.resolve(e);
        }
    }

}
