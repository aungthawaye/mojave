/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
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
 * ===
 */

package org.mojave.core.participant.store.strategy.timer;

import jakarta.annotation.PostConstruct;
import org.mojave.common.datatype.enums.participant.PartyIdType;
import org.mojave.common.datatype.identifier.participant.FspGroupId;
import org.mojave.common.datatype.identifier.participant.FspId;
import org.mojave.common.datatype.identifier.participant.OracleId;
import org.mojave.common.datatype.identifier.participant.SspId;
import org.mojave.common.datatype.type.participant.FspCode;
import org.mojave.common.datatype.type.participant.SspCode;
import org.mojave.core.participant.contract.data.FspData;
import org.mojave.core.participant.contract.data.FspGroupData;
import org.mojave.core.participant.contract.data.OracleData;
import org.mojave.core.participant.contract.data.SspData;
import org.mojave.core.participant.contract.query.FspGroupQuery;
import org.mojave.core.participant.contract.query.FspQuery;
import org.mojave.core.participant.contract.query.OracleQuery;
import org.mojave.core.participant.contract.query.SspQuery;
import org.mojave.core.participant.store.ParticipantStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TimerBasedInMemoryParticipantStore implements ParticipantStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        TimerBasedInMemoryParticipantStore.class);

    private final FspQuery fspQuery;

    private final FspGroupQuery fspGroupQuery;

    private final SspQuery sspQuery;

    private final OracleQuery oracleQuery;

    private final TimerBasedInMemoryParticipantStore.Settings settings;

    private final AtomicReference<Snapshot> snapshotRef = new AtomicReference<>(Snapshot.empty());

    private final Timer timer = new Timer("TimerBasedInMemoryParticipantStore", true);

    public TimerBasedInMemoryParticipantStore(FspQuery fspQuery,
                                              FspGroupQuery fspGroupQuery,
                                              SspQuery sspQuery,
                                              OracleQuery oracleQuery,
                                              TimerBasedInMemoryParticipantStore.Settings settings) {

        Objects.requireNonNull(fspQuery);
        Objects.requireNonNull(fspGroupQuery);
        Objects.requireNonNull(sspQuery);
        Objects.requireNonNull(oracleQuery);
        Objects.requireNonNull(settings);

        this.fspQuery = fspQuery;
        this.fspGroupQuery = fspGroupQuery;
        this.sspQuery = sspQuery;
        this.oracleQuery = oracleQuery;
        this.settings = settings;

    }

    @PostConstruct
    public void bootstrap() {

        var interval = this.settings.refreshIntervalMs();

        LOGGER.info("Bootstrapping TimerBasedInMemoryParticipantStore");
        this.refreshData();

        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                TimerBasedInMemoryParticipantStore.this.refreshData();
            }
        }, interval, interval);
    }

    @Override
    public FspData getFspData(FspId fspId) {

        if (fspId == null) {
            return null;
        }

        return this.snapshotRef.get().fspById.get(fspId);
    }

    @Override
    public FspData getFspData(FspCode fspCode) {

        if (fspCode == null) {
            return null;
        }

        return this.snapshotRef.get().fspByCode.get(fspCode);
    }

    @Override
    public FspGroupData getFspGroupData(FspGroupId fspGroupId) {

        return this.snapshotRef.get().fspGroupById.get(fspGroupId);
    }

    @Override
    public FspGroupData getFspGroupData(FspId fspId) {

        return this.snapshotRef.get().fspGroupByFspId.get(fspId);
    }

    @Override
    public OracleData getOracleData(OracleId oracleId) {

        if (oracleId == null) {
            return null;
        }

        return this.snapshotRef.get().oracleById.get(oracleId);
    }

    @Override
    public OracleData getOracleData(PartyIdType partyIdType) {

        if (partyIdType == null) {
            return null;
        }

        return this.snapshotRef.get().oracleByPartyIdType.get(partyIdType);
    }

    @Override
    public SspData getSspData(SspId sspId) {

        if (sspId == null) {
            return null;
        }

        return this.snapshotRef.get().sspById.get(sspId);
    }

    @Override
    public SspData getSspData(SspCode sspCode) {

        if (sspCode == null) {
            return null;
        }

        return this.snapshotRef.get().sspByCode.get(sspCode);
    }

    private void refreshData() {

        LOGGER.info("Start refreshing participant data");

        // Fetch all FSPs and populate maps
        List<FspData> fsps = this.fspQuery.getAll();
        // Fetch all FSP Groups
        List<FspGroupData> fspGroups = this.fspGroupQuery.getAll();
        // Fetch all SSPs and populate maps
        List<SspData> ssps = this.sspQuery.getAll();
        // Fetch all Oracles and populate maps
        List<OracleData> oracles = this.oracleQuery.getAll();

        var _fspById = fsps
                           .stream()
                           .collect(
                               Collectors.toUnmodifiableMap(
                                   FspData::fspId, Function.identity(), (a, b) -> a));

        var _fspByCode = fsps
                             .stream()
                             .collect(
                                 Collectors.toUnmodifiableMap(
                                     FspData::code, Function.identity(), (a, b) -> a));

        var _fspGroupById = fspGroups
                                .stream()
                                .collect(Collectors.toUnmodifiableMap(
                                    FspGroupData::fspGroupId,
                                    Function.identity(), (a, b) -> a));

        var _fspGroupByFspId = fsps
                                   .stream()
                                   .filter(fsp -> fsp.fspGroupId() != null)
                                   .collect(Collectors.toUnmodifiableMap(
                                       FspData::fspId, fsp -> _fspGroupById.get(fsp.fspGroupId()),
                                       (a, b) -> a));

        var _sspById = ssps
                           .stream()
                           .collect(
                               Collectors.toUnmodifiableMap(
                                   SspData::sspId, Function.identity(), (a, b) -> a));

        var _sspByCode = ssps
                             .stream()
                             .collect(
                                 Collectors.toUnmodifiableMap(
                                     SspData::code, Function.identity(), (a, b) -> a));

        var _oracleById = oracles
                              .stream()
                              .collect(Collectors.toUnmodifiableMap(
                                  OracleData::oracleId,
                                  Function.identity(), (a, b) -> a));

        var _oracleByPartyIdType = oracles
                                       .stream()
                                       .collect(Collectors.toUnmodifiableMap(
                                           OracleData::type,
                                           Function.identity(), (a, b) -> a));

        LOGGER.info(
            "Refreshed FSP count: {} | SSP count: {} | Oracle count: {}", fsps.size(),
            ssps.size(), oracles.size());

        this.snapshotRef.set(
            new Snapshot(
                _fspById, _fspByCode, _fspGroupById, _fspGroupByFspId, _sspById, _sspByCode,
                _oracleById, _oracleByPartyIdType));
    }

    private record Snapshot(Map<FspId, FspData> fspById,
                            Map<FspCode, FspData> fspByCode,
                            Map<FspGroupId, FspGroupData> fspGroupById,
                            Map<FspId, FspGroupData> fspGroupByFspId,
                            Map<SspId, SspData> sspById,
                            Map<SspCode, SspData> sspByCode,
                            Map<OracleId, OracleData> oracleById,
                            Map<PartyIdType, OracleData> oracleByPartyIdType) {

        static Snapshot empty() {

            return new Snapshot(
                Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of());
        }

    }

    public record Settings(int refreshIntervalMs) { }

}
