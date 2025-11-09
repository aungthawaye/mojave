package io.mojaloop.core.wallet.store.local;

import io.mojaloop.core.common.datatype.identifier.wallet.PositionId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.wallet.contract.data.PositionData;
import io.mojaloop.core.wallet.intercom.client.api.GetPositions;
import io.mojaloop.core.wallet.intercom.client.exception.WalletIntercomClientException;
import io.mojaloop.core.wallet.store.PositionStore;
import io.mojaloop.core.wallet.store.WalletStoreConfiguration;
import io.mojaloop.fspiop.spec.core.Currency;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PositionLocalStore implements PositionStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(PositionLocalStore.class);

    private final GetPositions getPositions;

    private final WalletStoreConfiguration.Settings walletStoreSettings;

    private final AtomicReference<Snapshot> snapshotRef = new AtomicReference<>(Snapshot.empty());

    private final Timer timer = new Timer("PositionLocalStoreRefreshTimer", true);

    public PositionLocalStore(final GetPositions getPositions, final WalletStoreConfiguration.Settings walletStoreSettings) {

        assert getPositions != null;
        assert walletStoreSettings != null;

        this.getPositions = getPositions;
        this.walletStoreSettings = walletStoreSettings;
    }

    private static String key(final WalletOwnerId walletOwnerId, final Currency currency) {

        return walletOwnerId.getId().toString() + ":" + currency.name();
    }

    @PostConstruct
    public void bootstrap() {

        var interval = this.walletStoreSettings.refreshIntervalMs();

        LOGGER.info("Bootstrapping PositionLocalStore");
        this.refreshData();

        this.timer.scheduleAtFixedRate(
            new TimerTask() {

                @Override
                public void run() {

                    PositionLocalStore.this.refreshData();
                }
            }, interval, interval);
    }

    @Override
    public PositionData get(final PositionId positionId) {

        if (positionId == null) {
            return null;
        }

        return this.snapshotRef.get().withPositionId.get(positionId);
    }

    @Override
    public PositionData get(final WalletOwnerId walletOwnerId, final Currency currency) {

        if (walletOwnerId == null || currency == null) {
            return null;
        }

        var key = key(walletOwnerId, currency);
        return this.snapshotRef.get().withOwnerCurrency.get(key);
    }

    @Override
    public Set<PositionData> get(final WalletOwnerId walletOwnerId) {

        if (walletOwnerId == null) {
            return Set.of();
        }

        return this.snapshotRef.get().withOwnerId.getOrDefault(walletOwnerId, Set.of());
    }

    private void refreshData() {

        try {

            LOGGER.info("Start refreshing position data");

            List<PositionData> positions = this.getPositions.execute();

            var _withPositionId = positions.stream().collect(Collectors.toUnmodifiableMap(PositionData::positionId, Function.identity(), (a, b) -> a));

            var _withOwnerId = Collections.unmodifiableMap(
                positions.stream().collect(Collectors.groupingBy(PositionData::walletOwnerId, Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet))));

            var _withOwnerCurrency = positions.stream().collect(Collectors.toUnmodifiableMap(w -> key(w.walletOwnerId(), w.currency()), Function.identity(), (a, b) -> a));

            LOGGER.info("Refreshed Position data, count: {}", positions.size());

            this.snapshotRef.set(new Snapshot(_withPositionId, _withOwnerId, _withOwnerCurrency));

        } catch (WalletIntercomClientException e) {
            throw new RuntimeException(e);
        }
    }

    private record Snapshot(Map<PositionId, PositionData> withPositionId, Map<WalletOwnerId, Set<PositionData>> withOwnerId, Map<String, PositionData> withOwnerCurrency) {

        static Snapshot empty() {

            return new Snapshot(Map.of(), Map.of(), Map.of());
        }

    }

}
