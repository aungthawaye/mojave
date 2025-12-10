package org.mojave.core.transfer.contract.command.step.fspiop;

import org.mojave.core.common.datatype.identifier.transfer.UdfTransferId;
import org.mojave.core.participant.contract.data.FspData;
import org.mojave.fspiop.common.data.Agreement;
import org.mojave.fspiop.common.exception.FspiopException;
import org.mojave.fspiop.spec.core.TransfersPostRequest;

import java.time.Instant;

public interface UnwrapRequestStep {

    Output execute(Input input) throws FspiopException;

    record Input(String context,
                 UdfTransferId udfTransferId,
                 FspData payerFsp,
                 FspData payeeFsp,
                 TransfersPostRequest request) { }

    record Output(String ilpCondition,
                  String ilpPacket,
                  Agreement agreement,
                  Instant requestExpiration) { }

}
