package io.mojaloop.core.transfer.contract.command;

import io.mojaloop.core.common.datatype.identifier.transfer.UdfTransferId;
import io.mojaloop.fspiop.service.component.FspiopHttpRequest;
import io.mojaloop.fspiop.spec.core.TransfersIDPutResponse;

public interface PutTransfersCommand {

    Output execute(Input input);

    record Input(FspiopHttpRequest request, UdfTransferId udfTransferId, TransfersIDPutResponse transfersIDPutResponse) { }

    record Output() { }

}
