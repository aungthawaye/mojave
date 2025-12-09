package io.mojaloop.core.transfer.contract.command.step.fspiop;

import io.mojaloop.core.common.datatype.identifier.transfer.UdfTransferId;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.transfer.contract.component.interledger.PartyUnwrapper;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.component.interledger.Interledger;
import io.mojaloop.fspiop.spec.core.Currency;
import io.mojaloop.fspiop.spec.core.PartyIdInfo;
import io.mojaloop.fspiop.spec.core.TransfersPostRequest;

import java.math.BigDecimal;
import java.time.Instant;

public interface UnwrapRequestStep {

    Output execute(Input input) throws FspiopException;

    record Input(String context,
                 UdfTransferId udfTransferId,
                 FspData payerFsp,
                 FspData payeeFsp,
                 TransfersPostRequest request) { }

    record Output(PartyIdInfo payerPartyIdInfo,
                  PartyIdInfo payeePartyIdInfo,
                  Currency currency,
                  BigDecimal transferAmount,
                  String ilpPacket,
                  String ilpCondition,
                  Instant requestExpiration) { }
}
