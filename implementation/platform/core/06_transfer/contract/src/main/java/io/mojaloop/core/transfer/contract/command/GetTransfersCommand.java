package io.mojaloop.core.transfer.contract.command;

import io.mojaloop.core.common.datatype.identifier.transfer.UdfTransferId;
import io.mojaloop.fspiop.service.component.FspiopHttpRequest;

public interface GetTransfersCommand {

    Output execute(Input input);

    record Input(FspiopHttpRequest request, UdfTransferId udfTransferId) { }

    record Output() { }

}
