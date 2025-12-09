package io.mojaloop.core.transfer.contract.command.step.stateful;

import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.transfer.TransferId;
import io.mojaloop.core.common.datatype.identifier.transfer.UdfTransferId;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.spec.core.Currency;
import io.mojaloop.fspiop.spec.core.ExtensionList;
import io.mojaloop.fspiop.spec.core.PartyIdInfo;

import java.math.BigDecimal;
import java.time.Instant;

public interface ReceiveTransferStep {

    Output execute(Input input) throws FspiopException;

    record Input(String context,
                 UdfTransferId udfTransferId,
                 FspData payerFsp,
                 FspData payeeFsp,
                 PartyIdInfo payerPartyIdInfo,
                 PartyIdInfo payeePartyIdInfo,
                 Currency currency,
                 BigDecimal transferAmount,
                 String ilpPacket,
                 String ilpCondition,
                 Instant requestExpiration,
                 ExtensionList extensionList) { }

    record Output(TransactionId transactionId,
                  Instant transactionAt,
                  TransferId transferId) { }
}
