package org.mojave.wallet.domain;

import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.wallet.WalletId;
import org.mojave.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.wallet.contract.data.WalletData;
import org.mojave.wallet.domain.cache.WalletCache;
import org.mojave.wallet.domain.model.Wallet;
import org.mojave.wallet.domain.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.util.Set;
import java.util.stream.Collectors;

public class WalletDomainDependencies implements WalletDomainConfiguration.RequiredDependencies {

    @Autowired
    private WalletRepository walletRepository;

    @Bean
    @Override
    public WalletCache walletCache() {

        return new RepositoryBackedWalletCache(this.walletRepository);
    }

    @Bean
    @Override
    public WalletDomainSettings.TestWalletEngine walletEngine() {

        return new WalletDomainSettings.TestWalletEngine();
    }

    private record RepositoryBackedWalletCache(WalletRepository walletRepository)
        implements WalletCache {

        @Override
        public WalletData get(final WalletId walletId) {

            return this.walletRepository.findById(walletId).map(Wallet::convert).orElse(null);
        }

        @Override
        public WalletData get(final WalletOwnerId walletOwnerId, final Currency currency) {

            return this.walletRepository
                       .findOne(WalletRepository.Filters
                                    .withOwnerId(walletOwnerId)
                                    .and(WalletRepository.Filters.withCurrency(currency))
                                    .and(WalletRepository.Filters.withScenario(Wallet.DEFAULT_SCENARIO)))
                       .map(Wallet::convert)
                       .orElse(null);
        }

        @Override
        public Set<WalletData> get(final WalletOwnerId walletOwnerId) {

            return this.walletRepository
                       .findAll(WalletRepository.Filters.withOwnerId(walletOwnerId))
                       .stream()
                       .map(Wallet::convert)
                       .collect(Collectors.toSet());
        }

    }

}
