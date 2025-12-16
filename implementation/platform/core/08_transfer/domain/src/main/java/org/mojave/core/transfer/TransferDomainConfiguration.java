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

package org.mojave.core.transfer;

import org.mojave.component.jpa.routing.RoutingJpaConfiguration;
import org.mojave.component.misc.MiscConfiguration;
import org.mojave.core.accounting.producer.AccountingProducerConfiguration;
import org.mojave.core.participant.store.ParticipantStoreConfiguration;
import org.mojave.core.transaction.producer.TransactionProducerConfiguration;
import org.mojave.core.transfer.contract.component.interledger.AgreementUnwrapper;
import org.mojave.core.wallet.producer.WalletProducerConfiguration;
import org.mojave.fspiop.component.FspiopComponentConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan(basePackages = {"org.mojave.core.transfer.domain"})
@Import(
    value = {
        MiscConfiguration.class,
        FspiopComponentConfiguration.class,
        AccountingProducerConfiguration.class,
        WalletProducerConfiguration.class,
        TransactionProducerConfiguration.class,
        ParticipantStoreConfiguration.class,
        RoutingJpaConfiguration.class})
public class TransferDomainConfiguration {

    public interface RequiredBeans extends MiscConfiguration.RequiredBeans,
                                           FspiopComponentConfiguration.RequiredBeans,
                                           AccountingProducerConfiguration.RequiredBeans,
                                           WalletProducerConfiguration.RequiredBeans,
                                           TransactionProducerConfiguration.RequiredBeans,
                                           RoutingJpaConfiguration.RequiredBeans,
                                           ParticipantStoreConfiguration.RequiredBeans {

        AgreementUnwrapper partyUnwrapper();

    }

    public interface RequiredSettings extends MiscConfiguration.RequiredSettings,
                                              FspiopComponentConfiguration.RequiredSettings,
                                              AccountingProducerConfiguration.RequiredSettings,
                                              WalletProducerConfiguration.RequiredSettings,
                                              TransactionProducerConfiguration.RequiredSettings,
                                              ParticipantStoreConfiguration.RequiredSettings,
                                              RoutingJpaConfiguration.RequiredSettings {

        TransferSettings transferSettings();

    }

    public record TransferSettings(int reservationTimeoutMs, int expiryTimeoutMs) { }

}
