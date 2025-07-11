package io.mojaloop.common.fspiop.client.api.transfers;

import io.mojaloop.common.fspiop.model.core.ErrorInformationObject;
import io.mojaloop.common.fspiop.model.core.TransfersIDPutResponse;
import io.mojaloop.common.fspiop.support.Destination;

public interface PutTransfers {

    void putTransfers(Destination destination, String transferId, TransfersIDPutResponse transfersIDPutResponse);

    void putTransfersError(Destination destination, String transferId, ErrorInformationObject errorInformationObject);

}
