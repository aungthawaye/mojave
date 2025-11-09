package io.mojaloop.core.wallet.store;

import io.mojaloop.core.common.datatype.identifier.wallet.PositionId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.wallet.contract.data.PositionData;
import io.mojaloop.core.wallet.contract.data.WalletData;
import io.mojaloop.fspiop.spec.core.Currency;

import java.util.Set;

public interface WalletStore {

    WalletData get(WalletId walletId);

    WalletData get(WalletOwnerId walletOwnerId, Currency currency);

    Set<WalletData> get(WalletOwnerId walletOwnerId);

}
