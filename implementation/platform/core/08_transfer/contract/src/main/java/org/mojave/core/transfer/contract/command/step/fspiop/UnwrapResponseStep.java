package org.mojave.core.transfer.contract.command.step.fspiop;

import org.mojave.fspiop.common.exception.FspiopException;
import org.mojave.fspiop.spec.core.TransferState;
import org.mojave.fspiop.spec.core.TransfersIDPutResponse;

import java.time.Instant;

public interface UnwrapResponseStep {

    Output execute(Input input) throws FspiopException;

    record Input(TransfersIDPutResponse response) { }

    record Output(TransferState state, String ilpFulfilment, Instant completedAt) { }

}
