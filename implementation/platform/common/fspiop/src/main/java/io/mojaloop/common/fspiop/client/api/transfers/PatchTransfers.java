package io.mojaloop.common.fspiop.client.api.transfers;

import io.mojaloop.common.fspiop.core.model.TransfersIDPatchResponse;
import io.mojaloop.common.fspiop.support.Destination;

public interface PatchTransfers {

    void patchTransfers(Destination destination, String transferId, TransfersIDPatchResponse transfersIDPatchResponse);

}
