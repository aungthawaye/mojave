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
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.participant.ParticipantContext;
import io.mojaloop.fspiop.common.type.Payer;
import io.mojaloop.fspiop.component.handy.FspiopHeaders;
import io.mojaloop.fspiop.component.retrofit.FspiopErrorDecoder;
import io.mojaloop.fspiop.invoker.api.QuotesService;
import io.mojaloop.fspiop.spec.core.ErrorInformationObject;
import io.mojaloop.fspiop.spec.core.QuotesIDPutResponse;
import org.springframework.stereotype.Service;

@Service
class PutQuotesHandler implements PutQuotes {

    private final ParticipantContext participantContext;

    private final QuotesService quotesService;

    private final FspiopErrorDecoder fspiopErrorDecoder;

    public PutQuotesHandler(ParticipantContext participantContext, QuotesService quotesService, FspiopErrorDecoder fspiopErrorDecoder) {

        assert participantContext != null;
        assert quotesService != null;
        assert fspiopErrorDecoder != null;

        this.participantContext = participantContext;
        this.quotesService = quotesService;
        this.fspiopErrorDecoder = fspiopErrorDecoder;
    }

    @Override
    public void putQuotes(Payer payer, String quoteId, QuotesIDPutResponse quotesIDPutResponse) throws FspiopException {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Quotes.forResult(this.participantContext.fspCode(), payer.fspCode());

            RetrofitService.invoke(this.quotesService.putQuotes(fspiopHeaders, quoteId, quotesIDPutResponse), this.fspiopErrorDecoder);

        } catch (RetrofitService.InvocationException e) {

            throw new FspiopException(FspiopErrors.GENERIC_CLIENT_ERROR, e);
        }
    }

    @Override
    public void putQuotesError(Payer payer, String quoteId, ErrorInformationObject errorInformationObject) throws FspiopException {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Quotes.forResult(this.participantContext.fspCode(), payer.fspCode());

            RetrofitService.invoke(this.quotesService.putQuotesError(fspiopHeaders, quoteId, errorInformationObject), this.fspiopErrorDecoder);

        } catch (RetrofitService.InvocationException e) {

            throw new FspiopException(FspiopErrors.GENERIC_CLIENT_ERROR, e);
        }
    }

}
