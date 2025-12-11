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
package org.mojave.platform.core.transfer.service;

import org.mojave.core.common.datatype.type.participant.FspCode;
import org.mojave.core.participant.contract.query.FspQuery;
import org.mojave.core.participant.contract.query.OracleQuery;
import org.mojave.core.participant.store.ParticipantStore;
import org.mojave.core.participant.store.strategy.timer.ParticipantTimerStore;
import org.mojave.core.transfer.contract.component.interledger.AgreementUnwrapper;
import org.mojave.core.transfer.domain.component.interledger.unwrapper.MojaveAgreementUnwrapper;
import org.mojave.fspiop.service.component.ParticipantVerifier;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.ObjectMapper;

public class TransferServiceDependencies
    implements TransferServiceConfiguration.RequiredDependencies {

    private final ParticipantStore participantStore;

    private final ObjectMapper objectMapper;

    public TransferServiceDependencies(FspQuery fspQuery,
                                       OracleQuery oracleQuery,
                                       ObjectMapper objectMapper) {

        assert fspQuery != null;
        assert oracleQuery != null;
        assert objectMapper != null;

        this.participantStore = new ParticipantTimerStore(
            fspQuery, oracleQuery, new ParticipantTimerStore.Settings(
            Integer.parseInt(System.getenv("PARTICIPANT_STORE_REFRESH_INTERVAL_MS"))));

        this.objectMapper = objectMapper;
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
