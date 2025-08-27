package io.mojaloop.connector.gateway.inbound.command.transfers;

import io.mojaloop.fspiop.common.type.Source;
import io.mojaloop.fspiop.spec.core.TransfersIDPutResponse;

public interface HandleTransfersResponseCommand {

    Output execute(Input input);

    record Input(Source source, String transferId, TransfersIDPutResponse response) { }

    record Output() { }

}