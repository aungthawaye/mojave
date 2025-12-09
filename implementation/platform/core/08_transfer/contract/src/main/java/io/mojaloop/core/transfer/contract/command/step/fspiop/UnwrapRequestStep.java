package io.mojaloop.core.transfer.contract.command.step.fspiop;

import io.mojaloop.core.common.datatype.identifier.transfer.UdfTransferId;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.fspiop.common.data.Agreement;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.spec.core.TransfersPostRequest;

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
