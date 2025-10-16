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

package io.mojaloop.fspiop.service.api.parties;

import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.participant.ParticipantContext;
import io.mojaloop.fspiop.common.type.Payer;
import io.mojaloop.fspiop.component.handy.FspiopHeaders;
import io.mojaloop.fspiop.component.retrofit.FspiopErrorDecoder;
import io.mojaloop.fspiop.component.retrofit.FspiopInvocationExceptionHandler;
import io.mojaloop.fspiop.service.api.PartiesResponseService;
import io.mojaloop.fspiop.spec.core.ErrorInformationObject;
import io.mojaloop.fspiop.spec.core.PartiesTypeIDPutResponse;
import org.springframework.stereotype.Service;

@Service
public class RespondPartiesHandler implements RespondParties {

    private final ParticipantContext participantContext;

    private final PartiesResponseService partiesResponseService;

    private final FspiopErrorDecoder fspiopErrorDecoder;

    private final FspiopInvocationExceptionHandler fspiopInvocationExceptionHandler;

    public RespondPartiesHandler(ParticipantContext participantContext,
                                 PartiesResponseService partiesResponseService,
                                 FspiopErrorDecoder fspiopErrorDecoder,
                                 FspiopInvocationExceptionHandler fspiopInvocationExceptionHandler) {

        assert participantContext != null;
        assert partiesResponseService != null;
        assert fspiopErrorDecoder != null;
        assert fspiopInvocationExceptionHandler != null;

        this.participantContext = participantContext;
        this.partiesResponseService = partiesResponseService;
        this.fspiopErrorDecoder = fspiopErrorDecoder;
        this.fspiopInvocationExceptionHandler = fspiopInvocationExceptionHandler;
    }

    @Override
    public void putParties(Payer payer, String url, PartiesTypeIDPutResponse response) throws FspiopException {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Parties.forResult(this.participantContext.fspCode(), payer.fspCode());

            RetrofitService.invoke(this.partiesResponseService.putParties(url, fspiopHeaders, response), this.fspiopErrorDecoder);

        } catch (RetrofitService.InvocationException e) {

            throw this.fspiopInvocationExceptionHandler.handle(e);
        }
    }

    @Override
    public void putPartiesError(Payer payer, String url, ErrorInformationObject error) throws FspiopException {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Parties.forResult(this.participantContext.fspCode(), payer.fspCode());

            RetrofitService.invoke(this.partiesResponseService.putPartiesError(url, fspiopHeaders, error), this.fspiopErrorDecoder);

        } catch (RetrofitService.InvocationException e) {

            throw this.fspiopInvocationExceptionHandler.handle(e);
        }
    }

}
