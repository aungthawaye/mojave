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

package io.mojaloop.core.participant.store.local;

import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.common.datatype.identifier.participant.OracleId;
import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.contract.data.OracleData;
import io.mojaloop.core.participant.intercom.client.api.GetFsps;
import io.mojaloop.core.participant.intercom.client.api.GetOracles;
import io.mojaloop.core.participant.intercom.client.exception.ParticipantIntercomClientException;
import io.mojaloop.core.participant.store.ParticipantStore;
import io.mojaloop.core.participant.store.ParticipantStoreConfiguration;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ParticipantLocalStore implements ParticipantStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParticipantLocalStore.class);

    private final GetFsps getFsps;

    private final GetOracles getOracles;

    private final ParticipantStoreConfiguration.Settings participantStoreSettings;

    private final Map<FspId, FspData> withFspId = new ConcurrentHashMap<>();

    private final Map<FspCode, FspData> withFspCode = new ConcurrentHashMap<>();

    private final Map<OracleId, OracleData> withOracleId = new ConcurrentHashMap<>();

    private final Map<PartyIdType, OracleData> withPartyIdType = new ConcurrentHashMap<>();

    private final Timer timer = new Timer("ParticipantLocalStoreRefreshTimer", true);

    public ParticipantLocalStore(GetFsps getFsps, GetOracles getOracles, ParticipantStoreConfiguration.Settings participantStoreSettings) {

        assert getFsps != null;
        assert getOracles != null;
        assert participantStoreSettings != null;

        this.getFsps = getFsps;
        this.getOracles = getOracles;
        this.participantStoreSettings = participantStoreSettings;

    }

    @PostConstruct
    public void bootstrap() {

        var interval = this.participantStoreSettings.refreshIntervalMs();

        LOGGER.info("Bootstrapping ParticipantLocalStore");
        this.refreshData();

        // Schedule a timer to refresh data every 30 seconds
        this.timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                ParticipantLocalStore.this.refreshData();
            }
        }, interval, interval); // 30 seconds in milliseconds
    }

    @Override
    public FspData getFspData(FspId fspId) {

        if (fspId == null) {
            return null;
        }

        return this.withFspId.get(fspId);
    }

    @Override
    public FspData getFspData(FspCode fspCode) {

        if (fspCode == null) {
            return null;
        }

        return this.withFspCode.get(fspCode);
    }

    @Override
    public OracleData getOracleData(OracleId oracleId) {

        if (oracleId == null) {
            return null;
        }

        return this.withOracleId.get(oracleId);
    }

    @Override
    public OracleData getOracleData(PartyIdType partyIdType) {

        if (partyIdType == null) {
            return null;
        }

        return this.withPartyIdType.get(partyIdType);
    }

    private void refreshData() {

        try {

            LOGGER.info("Start refreshing participant data");

            // Fetch all FSPs and populate maps
            List<FspData> fsps = this.getFsps.execute();

            // Clear existing maps before populating
            this.withFspId.clear();
            this.withFspCode.clear();

            for (FspData fsp : fsps) {
                this.withFspId.put(fsp.fspId(), fsp);
                this.withFspCode.put(fsp.fspCode(), fsp);
            }

            LOGGER.info("Refreshed FSP data, count: {}", fsps.size());

            // Fetch all Oracles and populate maps
            List<OracleData> oracles = this.getOracles.execute();

            // Clear existing maps before populating
            this.withOracleId.clear();
            this.withPartyIdType.clear();

            for (OracleData oracle : oracles) {
                this.withOracleId.put(oracle.oracleId(), oracle);
                this.withPartyIdType.put(oracle.type(), oracle);
            }

            LOGGER.info("Refreshed Oracle data, count: {}", oracles.size());

        } catch (ParticipantIntercomClientException e) {
            throw new RuntimeException(e);
        }
    }

}
