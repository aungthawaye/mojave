package io.mojaloop.core.wallet.domain.cache;

import io.mojaloop.core.common.datatype.identifier.wallet.WalletId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.wallet.contract.data.WalletData;
import io.mojaloop.fspiop.spec.core.Currency;

import java.util.Set;

public interface WalletCache {

    WalletData get(WalletId walletId);

    WalletData get(WalletOwnerId walletOwnerId, Currency currency);

    Set<WalletData> get(WalletOwnerId walletOwnerId);

}
