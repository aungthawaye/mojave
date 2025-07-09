package io.mojaloop.common.fspiop.client.api.transfers;

import io.mojaloop.common.component.retrofit.RetrofitService;
import io.mojaloop.common.fspiop.component.FspiopHeaders;
import io.mojaloop.common.fspiop.core.model.ErrorInformationResponse;
import io.mojaloop.common.fspiop.core.model.TransfersIDPatchResponse;
import io.mojaloop.common.fspiop.service.TransferService;
import io.mojaloop.common.fspiop.support.Destination;
import io.mojaloop.common.fspiop.support.FspSettings;
import org.springframework.stereotype.Service;

@Service
class PatchTransfersHandler implements PatchTransfers {

    private final FspSettings fspSettings;

    private final TransferService transferService;

    private final RetrofitService.ErrorDecoder<ErrorInformationResponse> errorDecoder;

    public PatchTransfersHandler(FspSettings fspSettings,
                                 TransferService transferService,
                                 RetrofitService.ErrorDecoder<ErrorInformationResponse> errorDecoder) {

        assert fspSettings != null;
        assert transferService != null;
        assert errorDecoder != null;

        this.fspSettings = fspSettings;
        this.transferService = transferService;
        this.errorDecoder = errorDecoder;
    }

    @Override
    public void patchTransfers(Destination destination, String transferId, TransfersIDPatchResponse transfersIDPatchResponse) {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Transfers.forCallback(this.fspSettings.fspId(), destination.destinationFspId());

            RetrofitService.invoke(this.transferService.patchTransfers(fspiopHeaders, transferId, transfersIDPatchResponse),
                                   this.errorDecoder);

        } catch (RetrofitService.InvocationException e) {
            throw new RuntimeException(e);
        }
    }

}