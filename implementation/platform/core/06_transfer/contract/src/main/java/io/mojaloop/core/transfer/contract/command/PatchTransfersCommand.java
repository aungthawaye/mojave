package io.mojaloop.core.transfer.contract.command;

import io.mojaloop.core.common.datatype.identifier.transfer.UdfTransferId;
import io.mojaloop.fspiop.service.component.FspiopHttpRequest;
import io.mojaloop.fspiop.spec.core.TransfersIDPatchResponse;

public interface PatchTransfersCommand {

    Output execute(Input input);

    record Input(FspiopHttpRequest request, UdfTransferId udfTransferId, TransfersIDPatchResponse transfersIDPatchResponse) { }

    record Output() { }

}
