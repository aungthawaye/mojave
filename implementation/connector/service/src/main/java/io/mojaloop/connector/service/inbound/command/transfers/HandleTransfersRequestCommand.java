package io.mojaloop.connector.service.inbound.command.transfers;

import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Source;
import io.mojaloop.fspiop.spec.core.TransfersPostRequest;

public interface HandleTransfersRequestCommand {

    void execute(Input input) throws FspiopException;

    record Input(Source source, String transferId, TransfersPostRequest request) { }

}