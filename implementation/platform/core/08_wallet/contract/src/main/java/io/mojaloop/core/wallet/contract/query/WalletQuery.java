package io.mojaloop.core.wallet.contract.query;

import io.mojaloop.core.common.datatype.identifier.wallet.WalletId;
import io.mojaloop.core.wallet.contract.data.WalletData;

import java.util.List;

public interface WalletQuery {

    WalletData get(WalletId walletId);

    List<WalletData> getAll();

}
