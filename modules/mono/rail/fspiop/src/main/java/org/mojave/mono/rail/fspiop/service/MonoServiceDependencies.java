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

package org.mojave.mono.rail.fspiop.service;

import org.mojave.core.participant.contract.query.FspGroupQuery;
import org.mojave.core.participant.contract.query.FspQuery;
import org.mojave.core.participant.contract.query.OracleQuery;
import org.mojave.core.participant.contract.query.SspQuery;
import org.mojave.core.participant.store.ParticipantStore;
import org.mojave.core.participant.store.strategy.timer.LocalParticipantStore;
import org.mojave.core.wallet.contract.query.WalletQuery;
import org.mojave.core.wallet.store.WalletStore;
import org.mojave.core.wallet.store.strategy.timer.LocalWalletStore;
import org.mojave.rail.fspiop.component.participant.ParticipantContext;
import org.mojave.rail.fspiop.component.participant.loader.EnvBasedParticipantContextLoader;
import org.mojave.rail.fspiop.service.component.ParticipantVerifier;
import org.mojave.rail.fspiop.transfer.contract.component.interledger.AgreementUnwrapper;
import org.mojave.rail.fspiop.transfer.domain.component.interledger.unwrapper.MojaveAgreementUnwrapper;
import org.mojave.scheme.rule.type.participant.FspCode;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

public class MonoServiceDependencies implements MonoServiceConfiguration.RequiredDependencies {

    private final ObjectMapper objectMapper;

    private final ParticipantStore participantStore;

    private final WalletStore walletStore;

    public MonoServiceDependencies(ObjectMapper objectMapper, FspQuery fspQuery,
                                   FspGroupQuery fspGroupQuery, SspQuery sspQuery,
                                   OracleQuery oracleQuery, WalletQuery walletQuery) {

        Objects.requireNonNull(objectMapper);
        Objects.requireNonNull(fspQuery);
        Objects.requireNonNull(fspGroupQuery);
        Objects.requireNonNull(sspQuery);
        Objects.requireNonNull(oracleQuery);
        Objects.requireNonNull(walletQuery);

        this.objectMapper = objectMapper;

        this.participantStore = new LocalParticipantStore(
            fspQuery, fspGroupQuery, sspQuery, oracleQuery, new LocalParticipantStore.Settings(
            Integer.parseInt(System.getenv("PARTICIPANT_STORE_REFRESH_INTERVAL_MS"))));

        this.walletStore = new LocalWalletStore(
            walletQuery, new LocalWalletStore.Settings(
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

    @Bean
    @Override
    public AgreementUnwrapper partyUnwrapper() {

        return new MojaveAgreementUnwrapper(this.objectMapper);
    }

    @Bean
    @Override
    public WalletStore walletStore() {

        return this.walletStore;
    }

}
