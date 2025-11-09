package io.mojaloop.core.wallet.store;

import io.mojaloop.core.common.datatype.identifier.wallet.PositionId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.wallet.contract.data.PositionData;
import io.mojaloop.fspiop.spec.core.Currency;

import java.util.Set;

public interface PositionStore {

    PositionData get(PositionId positionId);

    PositionData get(WalletOwnerId walletOwnerId, Currency currency);

    Set<PositionData> get(WalletOwnerId walletOwnerId);

}
