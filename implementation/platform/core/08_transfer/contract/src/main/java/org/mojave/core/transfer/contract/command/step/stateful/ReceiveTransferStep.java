package org.mojave.core.transfer.contract.command.step.stateful;

import org.mojave.core.common.datatype.identifier.transaction.TransactionId;
import org.mojave.core.common.datatype.identifier.transfer.TransferId;
import org.mojave.core.common.datatype.identifier.transfer.UdfTransferId;
import org.mojave.core.participant.contract.data.FspData;
import org.mojave.fspiop.common.data.Agreement;
import org.mojave.fspiop.common.exception.FspiopException;
import org.mojave.fspiop.spec.core.ExtensionList;

import java.time.Instant;

public interface ReceiveTransferStep {

    Output execute(Input input) throws FspiopException;

    record Input(String context,
                 UdfTransferId udfTransferId,
                 FspData payerFsp,
                 FspData payeeFsp,
                 String ilpCondition,
                 String ilpPacket,
                 Agreement agreement,
                 Instant requestExpiration,
                 ExtensionList extensionList) { }

    record Output(TransactionId transactionId, Instant transactionAt, TransferId transferId) { }

}
