package io.mojaloop.common.fspiop.client.api.transfers;

import io.mojaloop.common.component.retrofit.RetrofitService;
import io.mojaloop.common.fspiop.component.FspiopHeaders;
import io.mojaloop.common.fspiop.core.model.ErrorInformationObject;
import io.mojaloop.common.fspiop.core.model.ErrorInformationResponse;
import io.mojaloop.common.fspiop.core.model.TransfersIDPutResponse;
import io.mojaloop.common.fspiop.service.TransferService;
import io.mojaloop.common.fspiop.support.Destination;
import io.mojaloop.common.fspiop.support.FspSettings;
import org.springframework.stereotype.Service;

@Service
class PutTransfersHandler implements PutTransfers {

    private final FspSettings fspSettings;

    private final TransferService transferService;

    private final RetrofitService.ErrorDecoder<ErrorInformationResponse> errorDecoder;

    public PutTransfersHandler(FspSettings fspSettings,
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
    public void putTransfers(Destination destination, String transferId, TransfersIDPutResponse transfersIDPutResponse) {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Transfers.forCallback(this.fspSettings.fspId(), destination.destinationFspId());

            RetrofitService.invoke(this.transferService.putTransfers(fspiopHeaders, transferId, transfersIDPutResponse), this.errorDecoder);

        } catch (RetrofitService.InvocationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void putTransfersError(Destination destination, String transferId, ErrorInformationObject errorInformationObject) {

        try {
            var fspiopHeaders = FspiopHeaders.Values.Transfers.forCallback(this.fspSettings.fspId(), destination.destinationFspId());

            RetrofitService.invoke(this.transferService.putTransfersError(fspiopHeaders, transferId, errorInformationObject),
                                   this.errorDecoder);

        } catch (RetrofitService.InvocationException e) {
            throw new RuntimeException(e);
        }
    }

}