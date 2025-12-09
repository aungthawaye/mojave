/*-
 * ================================================================================
 * Mojave
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */

package io.mojaloop.core.participant.store.strategy.timer;

import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.common.datatype.identifier.participant.OracleId;
import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.contract.data.OracleData;
import io.mojaloop.core.participant.contract.query.FspQuery;
import io.mojaloop.core.participant.contract.query.OracleQuery;
import io.mojaloop.core.participant.store.ParticipantStore;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ParticipantTimerStore implements ParticipantStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParticipantTimerStore.class);

    private final FspQuery fspQuery;

    private final OracleQuery oracleQuery;

    private final ParticipantTimerStore.Settings settings;

    private final AtomicReference<Snapshot> snapshotRef = new AtomicReference<>(Snapshot.empty());

    private final Timer timer = new Timer("ParticipantTimerStore", true);

    public ParticipantTimerStore(FspQuery fspQuery,
                                 OracleQuery oracleQuery,
                                 ParticipantTimerStore.Settings settings) {

        assert fspQuery != null;
        assert oracleQuery != null;
        assert settings != null;

        this.fspQuery = fspQuery;
        this.oracleQuery = oracleQuery;
        this.settings = settings;

    }

    @PostConstruct
    public void bootstrap() {

        var interval = this.settings.refreshIntervalMs();

        LOGGER.info("Bootstrapping ParticipantTimerStore");
        this.refreshData();

        this.timer.scheduleAtFixedRate(
            new TimerTask() {

                @Override
                public void run() {

                    ParticipantTimerStore.this.refreshData();
                }
            }, interval, interval); // 30 seconds in milliseconds
    }

    @Override
    public FspData getFspData(FspId fspId) {

        if (fspId == null) {
            return null;
        }

        return this.snapshotRef.get().withFspId.get(fspId);
    }

    @Override
    public FspData getFspData(FspCode fspCode) {

        if (fspCode == null) {
            return null;
        }

        return this.snapshotRef.get().withFspCode.get(fspCode);
    }

    @Override
    public OracleData getOracleData(OracleId oracleId) {

        if (oracleId == null) {
            return null;
        }

        return this.snapshotRef.get().withOracleId.get(oracleId);
    }

    @Override
    public OracleData getOracleData(PartyIdType partyIdType) {

        if (partyIdType == null) {
            return null;
        }

        return this.snapshotRef.get().withPartyIdType.get(partyIdType);
    }

    private void refreshData() {

        LOGGER.info("Start refreshing participant data");

        // Fetch all FSPs and populate maps
        List<FspData> fsps = this.fspQuery.getAll();
        // Fetch all Oracles and populate maps
        List<OracleData> oracles = this.oracleQuery.getAll();

        var _withFspId = fsps
                             .stream()
                             .collect(
                                 Collectors.toUnmodifiableMap(
                                     FspData::fspId, Function.identity(),
                                     (a, b) -> a));

        var _withFspCode = fsps
                               .stream()
                               .collect(Collectors.toUnmodifiableMap(
                                   FspData::fspCode,
                                   Function.identity(), (a, b) -> a));

        var _withOracleId = oracles
                                .stream()
                                .collect(Collectors.toUnmodifiableMap(
                                    OracleData::oracleId,
                                    Function.identity(), (a, b) -> a));

        var _withPartyIdType = oracles
                                   .stream()
                                   .collect(Collectors.toUnmodifiableMap(
                                       OracleData::type,
                                       Function.identity(), (a, b) -> a));

        LOGGER.info("Refreshed FSP count: {} | Oracle count: {}", fsps.size(), oracles.size());

        this.snapshotRef.set(
            new Snapshot(_withFspId, _withFspCode, _withOracleId, _withPartyIdType));
    }

    private record Snapshot(Map<FspId, FspData> withFspId,
                            Map<FspCode, FspData> withFspCode,
                            Map<OracleId, OracleData> withOracleId,
                            Map<PartyIdType, OracleData> withPartyIdType) {

        static Snapshot empty() {

            return new Snapshot(Map.of(), Map.of(), Map.of(), Map.of());
        }

    }

    public record Settings(int refreshIntervalMs) { }

}
