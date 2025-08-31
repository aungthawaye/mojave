package io.mojaloop.connector.gateway.outbound.command;

import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Destination;
import io.mojaloop.fspiop.spec.core.TransfersIDPutResponse;
import io.mojaloop.fspiop.spec.core.TransfersPostRequest;

public interface RequestTransfersCommand {

    Output execute(Input input) throws FspiopException;

    record Input(Destination destination, TransfersPostRequest request) { }

    record Output(TransfersIDPutResponse response) { }

}
