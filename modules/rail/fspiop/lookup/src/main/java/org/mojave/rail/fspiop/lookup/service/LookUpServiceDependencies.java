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

package org.mojave.rail.fspiop.lookup.service;

import org.mojave.core.participant.contract.query.FspGroupQuery;
import org.mojave.core.participant.contract.query.FspQuery;
import org.mojave.core.participant.contract.query.OracleQuery;
import org.mojave.core.participant.contract.query.SspQuery;
import org.mojave.core.participant.store.ParticipantStore;
import org.mojave.core.participant.store.strategy.timer.LocalParticipantStore;
import org.mojave.rail.fspiop.component.participant.ParticipantContext;
import org.mojave.rail.fspiop.component.participant.loader.EnvBasedParticipantContextLoader;
import org.mojave.rail.fspiop.foundation.component.ParticipantVerifier;
import org.mojave.scheme.rule.type.participant.FspCode;
import org.springframework.context.annotation.Bean;

import java.util.Objects;

public class LookUpServiceDependencies implements LookUpServiceConfiguration.RequiredDependencies {

    private final ParticipantStore participantStore;

    public LookUpServiceDependencies(FspQuery fspQuery, FspGroupQuery fspGroupQuery,
                                     SspQuery sspQuery, OracleQuery oracleQuery) {

        Objects.requireNonNull(fspQuery);
        Objects.requireNonNull(fspGroupQuery);
        Objects.requireNonNull(sspQuery);
        Objects.requireNonNull(oracleQuery);

        this.participantStore = new LocalParticipantStore(
            fspQuery, fspGroupQuery, sspQuery, oracleQuery,
            new LocalParticipantStore.Settings(
                Integer.parseInt(System.getenv("PARTICIPANT_STORE_REFRESH_INTERVAL_MS"))));
    }

    @Bean
    @Override
    public ParticipantContext participantContext() {

        var loader = new EnvBasedParticipantContextLoader();

        return loader.load();
    }

    @Bean
    @Override
    public ParticipantStore participantStore() {

        return this.participantStore;
    }

    @Bean
    @Override
    public ParticipantVerifier participantVerifier() {

        return fspCode -> this.participantStore.getFspData(new FspCode(fspCode)) != null;
    }

}
