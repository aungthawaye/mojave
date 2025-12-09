package io.mojaloop.core.transfer.contract.command.step.fspiop;

import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.spec.core.TransferState;
import io.mojaloop.fspiop.spec.core.TransfersIDPutResponse;

import java.time.Instant;

public interface UnwrapResponseStep {

    Output execute(Input input) throws FspiopException;

    record Input(TransfersIDPutResponse response) { }

    record Output(TransferState state, String ilpFulfilment, Instant completedAt) { }

}
