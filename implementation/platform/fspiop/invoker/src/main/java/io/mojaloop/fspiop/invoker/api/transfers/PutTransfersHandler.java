/*-
 * ================================================================================
 * Mojave
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

package io.mojaloop.fspiop.invoker.api.transfers;

import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.participant.ParticipantContext;
import io.mojaloop.fspiop.common.type.Payer;
import io.mojaloop.fspiop.component.handy.FspiopHeaders;
import io.mojaloop.fspiop.component.retrofit.FspiopErrorDecoder;
import io.mojaloop.fspiop.component.retrofit.FspiopInvocationExceptionResolver;
import io.mojaloop.fspiop.invoker.api.TransfersService;
import io.mojaloop.fspiop.spec.core.ErrorInformationObject;
import io.mojaloop.fspiop.spec.core.TransfersIDPutResponse;
import org.springframework.stereotype.Service;

@Service
class PutTransfersHandler implements PutTransfers {

    private final ParticipantContext participantContext;

    private final TransfersService transfersService;

    private final FspiopErrorDecoder fspiopErrorDecoder;

    public PutTransfersHandler(ParticipantContext participantContext,
                               TransfersService transfersService,
                               FspiopErrorDecoder fspiopErrorDecoder) {

        assert participantContext != null;
        assert transfersService != null;
        assert fspiopErrorDecoder != null;

        this.participantContext = participantContext;
        this.transfersService = transfersService;
        this.fspiopErrorDecoder = fspiopErrorDecoder;
    }

    @Override
    public void putTransfers(Payer payer,
                             String transferId,
                             TransfersIDPutResponse transfersIDPutResponse) throws FspiopException {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Transfers.forResult(
                this.participantContext.fspCode(), payer.fspCode());

            RetrofitService.invoke(
                this.transfersService.putTransfers(
                    fspiopHeaders, transferId, transfersIDPutResponse), this.fspiopErrorDecoder);

        } catch (RetrofitService.InvocationException e) {

            throw FspiopInvocationExceptionResolver.resolve(e);
        }
    }

    @Override
    public void putTransfersError(Payer payer,
                                  String transferId,
                                  ErrorInformationObject errorInformationObject)
        throws FspiopException {

        try {
            var fspiopHeaders = FspiopHeaders.Values.Transfers.forResult(
                this.participantContext.fspCode(), payer.fspCode());

            RetrofitService.invoke(
                this.transfersService.putTransfersError(
                    fspiopHeaders, transferId, errorInformationObject), this.fspiopErrorDecoder);

        } catch (RetrofitService.InvocationException e) {

            throw FspiopInvocationExceptionResolver.resolve(e);
        }
    }

}
