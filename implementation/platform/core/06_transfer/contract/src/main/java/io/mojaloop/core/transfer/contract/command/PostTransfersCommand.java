package io.mojaloop.core.transfer.contract.command;

import io.mojaloop.fspiop.service.component.FspiopHttpRequest;
import io.mojaloop.fspiop.spec.core.TransfersPostRequest;

public interface PostTransfersCommand {

    Output execute(Input input);

    record Input(FspiopHttpRequest request, TransfersPostRequest transfersPostRequest) { }

    record Output() { }

}
