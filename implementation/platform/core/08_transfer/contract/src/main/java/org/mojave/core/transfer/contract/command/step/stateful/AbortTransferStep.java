package org.mojave.core.transfer.contract.command.step.stateful;

import org.mojave.core.common.datatype.enums.Direction;
import org.mojave.core.common.datatype.enums.transfer.AbortReason;
import org.mojave.core.common.datatype.identifier.transaction.TransactionId;
import org.mojave.core.common.datatype.identifier.transfer.TransferId;
import org.mojave.core.common.datatype.identifier.wallet.PositionUpdateId;
import org.mojave.fspiop.common.exception.FspiopException;
import org.mojave.fspiop.spec.core.ExtensionList;

public interface AbortTransferStep {

    void execute(Input input) throws FspiopException;

    record Input(String context,
                 TransactionId transactionId,
                 TransferId transferId,
                 AbortReason abortReason,
                 PositionUpdateId rollbackId,
                 Direction direction,
                 ExtensionList extensionList) { }

}
