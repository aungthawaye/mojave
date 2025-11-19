package io.mojaloop.core.wallet.domain.cache.strategy.local;

import io.mojaloop.core.common.datatype.identifier.wallet.WalletId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.wallet.contract.data.WalletData;
import io.mojaloop.core.wallet.domain.cache.WalletCache;
import io.mojaloop.core.wallet.domain.repository.WalletRepository;
import io.mojaloop.fspiop.spec.core.Currency;
import jakarta.annotation.PostConstruct;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WalletLocalCache implements WalletCache {

    private final WalletRepository walletRepository;

    private final Map<Long, WalletData> withId;

    private final Map<String, WalletData> withOwnerCurrency;

    private final Map<Long, Set<WalletData>> withOwnerId;

    public WalletLocalCache(final WalletRepository walletRepository) {

        assert walletRepository != null;

        this.walletRepository = walletRepository;

        this.withId = new ConcurrentHashMap<>();
        this.withOwnerCurrency = new ConcurrentHashMap<>();
        this.withOwnerId = new ConcurrentHashMap<>();
    }

    @PostConstruct
    public void postConstruct() {

        this.clear();

        final var wallets = this.walletRepository.findAll();

        for (final var wallet : wallets) {
            this.save(wallet.convert());
        }
    }

    private void clear() {

        this.withId.clear();
        this.withOwnerCurrency.clear();
        this.withOwnerId.clear();
    }

    private void save(final WalletData wallet) {

        this.withId.put(wallet.walletId().getId(), wallet);

        final var key = key(wallet.walletOwnerId(), wallet.currency());
        this.withOwnerCurrency.put(key, wallet);

        final var set = this.withOwnerId.computeIfAbsent(wallet.walletOwnerId().getId(), __ -> Collections.newSetFromMap(new ConcurrentHashMap<>()));
        set.add(wallet);
    }

    @Override
    public WalletData get(final WalletId walletId) {

        if (walletId == null) {
            return null;
        }

        return this.withId.get(walletId.getId());
    }

    @Override
    public WalletData get(final WalletOwnerId walletOwnerId, final Currency currency) {

        if (walletOwnerId == null || currency == null) {
            return null;
        }

        final var key = key(walletOwnerId, currency);
        return this.withOwnerCurrency.get(key);
    }

    @Override
    public Set<WalletData> get(final WalletOwnerId walletOwnerId) {

        if (walletOwnerId == null) {
            return Set.of();
        }

        final var set = this.withOwnerId.get(walletOwnerId.getId());
        return set == null ? Set.of() : Set.copyOf(set);
    }

    private static String key(final WalletOwnerId walletOwnerId, final Currency currency) {

        return walletOwnerId.getId().toString() + ":" + currency.name();
    }
}
