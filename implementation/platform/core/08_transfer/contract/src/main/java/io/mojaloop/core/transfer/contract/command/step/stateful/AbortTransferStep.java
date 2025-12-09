package io.mojaloop.core.transfer.contract.command.step.stateful;

import io.mojaloop.core.common.datatype.enums.Direction;
import io.mojaloop.core.common.datatype.enums.transfer.AbortReason;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.transfer.TransferId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.spec.core.ExtensionList;

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
