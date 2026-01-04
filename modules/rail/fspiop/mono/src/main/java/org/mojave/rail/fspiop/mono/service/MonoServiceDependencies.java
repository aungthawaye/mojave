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
package org.mojave.rail.fspiop.mono.service;

import org.mojave.scheme.common.datatype.type.participant.FspCode;
import org.mojave.core.participant.contract.query.FspQuery;
import org.mojave.core.participant.contract.query.OracleQuery;
import org.mojave.core.participant.contract.query.SspQuery;
import org.mojave.core.participant.store.ParticipantStore;
import org.mojave.core.participant.store.strategy.timer.ParticipantTimerStore;
import org.mojave.rail.fspiop.transfer.contract.component.interledger.AgreementUnwrapper;
import org.mojave.rail.fspiop.transfer.domain.component.interledger.unwrapper.MojaveAgreementUnwrapper;
import org.mojave.rail.fspiop.bootstrap.component.ParticipantVerifier;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.ObjectMapper;

public class MonoServiceDependencies implements MonoServiceConfiguration.RequiredDependencies {

    private final ObjectMapper objectMapper;

    private final ParticipantStore participantStore;

    public MonoServiceDependencies(ObjectMapper objectMapper,
                                   FspQuery fspQuery,
                                   SspQuery sspQuery,
                                   OracleQuery oracleQuery) {

        assert objectMapper != null;
        assert fspQuery != null;
        assert sspQuery != null;
        assert oracleQuery != null;

        this.objectMapper = objectMapper;

        this.participantStore = new ParticipantTimerStore(
            fspQuery, sspQuery, oracleQuery, new ParticipantTimerStore.Settings(
            Integer.parseInt(System.getenv("PARTICIPANT_STORE_REFRESH_INTERVAL_MS"))));

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

    @Bean
    @Override
    public AgreementUnwrapper partyUnwrapper() {

        return new MojaveAgreementUnwrapper(this.objectMapper);
    }

}
