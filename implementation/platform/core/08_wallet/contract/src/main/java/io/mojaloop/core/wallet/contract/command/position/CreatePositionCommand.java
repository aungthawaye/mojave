package io.mojaloop.core.wallet.contract.command.position;

import io.mojaloop.core.common.datatype.identifier.wallet.PositionId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.fspiop.spec.core.Currency;

import java.math.BigDecimal;

public interface CreatePositionCommand {

    Output execute(Input input);

    record Input(WalletOwnerId walletOwnerId, Currency currency, String name, BigDecimal netDebitCap) { }

    record Output(PositionId positionId) { }
}
