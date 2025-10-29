package io.mojaloop.core.transfer.contract.command;

import io.mojaloop.core.common.datatype.identifier.transfer.UdfTransferId;
import io.mojaloop.fspiop.service.component.FspiopHttpRequest;
import io.mojaloop.fspiop.spec.core.ErrorInformationObject;

public interface PutTransfersErrorCommand {

    record Input(FspiopHttpRequest request, UdfTransferId udfTransferId, ErrorInformationObject error) { }

    record Output() { }

}
