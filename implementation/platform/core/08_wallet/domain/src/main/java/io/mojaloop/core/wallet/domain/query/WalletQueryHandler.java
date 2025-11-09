package io.mojaloop.core.wallet.domain.query;

import io.mojaloop.component.jpa.routing.annotation.Read;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletId;
import io.mojaloop.core.wallet.contract.data.WalletData;
import io.mojaloop.core.wallet.contract.exception.wallet.WalletIdNotFoundException;
import io.mojaloop.core.wallet.contract.query.WalletQuery;
import io.mojaloop.core.wallet.domain.model.Wallet;
import io.mojaloop.core.wallet.domain.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WalletQueryHandler implements WalletQuery {

    private final WalletRepository walletRepository;

    public WalletQueryHandler(final WalletRepository walletRepository) {

        assert walletRepository != null;

        this.walletRepository = walletRepository;
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public WalletData get(final WalletId walletId) {

        return this.walletRepository.findById(walletId).orElseThrow(() -> new WalletIdNotFoundException(walletId)).convert();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<WalletData> getAll() {

        return this.walletRepository.findAll().stream().map(Wallet::convert).toList();
    }

}
