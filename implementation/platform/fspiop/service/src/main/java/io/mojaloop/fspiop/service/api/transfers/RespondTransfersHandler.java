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

package io.mojaloop.fspiop.service.api.transfers;

import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.participant.ParticipantContext;
import io.mojaloop.fspiop.common.type.Payer;
import io.mojaloop.fspiop.component.handy.FspiopHeaders;
import io.mojaloop.fspiop.component.retrofit.FspiopErrorDecoder;
import io.mojaloop.fspiop.component.retrofit.FspiopInvocationExceptionHandler;
import io.mojaloop.fspiop.service.api.TransfersResponseService;
import io.mojaloop.fspiop.spec.core.ErrorInformationObject;
import io.mojaloop.fspiop.spec.core.TransfersIDPatchResponse;
import io.mojaloop.fspiop.spec.core.TransfersIDPutResponse;
import org.springframework.stereotype.Service;

@Service
public class RespondTransfersHandler implements RespondTransfers {

    private final ParticipantContext participantContext;

    private final TransfersResponseService transfersResponseService;

    private final FspiopErrorDecoder fspiopErrorDecoder;

    private final FspiopInvocationExceptionHandler fspiopInvocationExceptionHandler;

    public RespondTransfersHandler(ParticipantContext participantContext,
                                   TransfersResponseService transfersResponseService,
                                   FspiopErrorDecoder fspiopErrorDecoder,
                                   FspiopInvocationExceptionHandler fspiopInvocationExceptionHandler) {

        assert participantContext != null;
        assert transfersResponseService != null;
        assert fspiopErrorDecoder != null;
        assert fspiopInvocationExceptionHandler != null;

        this.participantContext = participantContext;
        this.transfersResponseService = transfersResponseService;
        this.fspiopErrorDecoder = fspiopErrorDecoder;
        this.fspiopInvocationExceptionHandler = fspiopInvocationExceptionHandler;
    }

    @Override
    public void patchTransfers(Payer payer, String url, TransfersIDPatchResponse response) throws FspiopException {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Transfers.forResult(this.participantContext.fspCode(), payer.fspCode());

            RetrofitService.invoke(this.transfersResponseService.patchTransfers(url, fspiopHeaders, response), this.fspiopErrorDecoder);

        } catch (RetrofitService.InvocationException e) {

            throw this.fspiopInvocationExceptionHandler.handle(e);
        }
    }

    @Override
    public void putTransfers(Payer payer, String url, TransfersIDPutResponse response) throws FspiopException {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Transfers.forResult(this.participantContext.fspCode(), payer.fspCode());

            RetrofitService.invoke(this.transfersResponseService.putTransfers(url, fspiopHeaders, response), this.fspiopErrorDecoder);

        } catch (RetrofitService.InvocationException e) {

            throw this.fspiopInvocationExceptionHandler.handle(e);
        }
    }

    @Override
    public void putTransfersError(Payer payer, String url, ErrorInformationObject error) throws FspiopException {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Transfers.forResult(this.participantContext.fspCode(), payer.fspCode());

            RetrofitService.invoke(this.transfersResponseService.putTransfersError(url, fspiopHeaders, error), this.fspiopErrorDecoder);

        } catch (RetrofitService.InvocationException e) {

            throw this.fspiopInvocationExceptionHandler.handle(e);
        }
    }

}
