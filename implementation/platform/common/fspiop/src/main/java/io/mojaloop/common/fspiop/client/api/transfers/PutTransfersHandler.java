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
package io.mojaloop.common.fspiop.client.api.transfers;

import io.mojaloop.common.component.retrofit.RetrofitService;
import io.mojaloop.common.fspiop.component.FspiopHeaders;
import io.mojaloop.common.fspiop.model.core.ErrorInformationObject;
import io.mojaloop.common.fspiop.model.core.ErrorInformationResponse;
import io.mojaloop.common.fspiop.model.core.TransfersIDPutResponse;
import io.mojaloop.common.fspiop.service.TransferService;
import io.mojaloop.common.fspiop.support.Destination;
import io.mojaloop.common.fspiop.support.ParticipantSettings;
import org.springframework.stereotype.Service;

@Service
class PutTransfersHandler implements PutTransfers {

    private final ParticipantSettings participantSettings;

    private final TransferService transferService;

    private final RetrofitService.ErrorDecoder<ErrorInformationResponse> errorDecoder;

    public PutTransfersHandler(ParticipantSettings participantSettings,
                               TransferService transferService,
                               RetrofitService.ErrorDecoder<ErrorInformationResponse> errorDecoder) {

        assert participantSettings != null;
        assert transferService != null;
        assert errorDecoder != null;

        this.participantSettings = participantSettings;
        this.transferService = transferService;
        this.errorDecoder = errorDecoder;
    }

    @Override
    public void putTransfers(Destination destination, String transferId, TransfersIDPutResponse transfersIDPutResponse) {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Transfers.forCallback(this.participantSettings.fspId(), destination.destinationFspId());

            RetrofitService.invoke(this.transferService.putTransfers(fspiopHeaders, transferId, transfersIDPutResponse), this.errorDecoder);

        } catch (RetrofitService.InvocationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void putTransfersError(Destination destination, String transferId, ErrorInformationObject errorInformationObject) {

        try {
            var fspiopHeaders = FspiopHeaders.Values.Transfers.forCallback(this.participantSettings.fspId(), destination.destinationFspId());

            RetrofitService.invoke(this.transferService.putTransfersError(fspiopHeaders, transferId, errorInformationObject),
                                   this.errorDecoder);

        } catch (RetrofitService.InvocationException e) {
            throw new RuntimeException(e);
        }
    }

}
