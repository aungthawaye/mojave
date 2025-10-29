package io.mojaloop.core.wallet.contract.command.wallet;

import io.mojaloop.core.common.datatype.identifier.wallet.WalletId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.fspiop.spec.core.Currency;

public interface CreateWalletCommand {

    Output execute(Input input);

    record Input(WalletOwnerId walletOwnerId, Currency currency, String name) { }

    record Output(WalletId walletId) { }

}
