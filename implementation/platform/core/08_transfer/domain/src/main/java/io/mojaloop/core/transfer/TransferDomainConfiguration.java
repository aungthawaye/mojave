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

package io.mojaloop.core.transfer;

import io.mojaloop.component.jpa.routing.RoutingJpaConfiguration;
import io.mojaloop.component.misc.MiscConfiguration;
import io.mojaloop.core.participant.store.ParticipantStoreConfiguration;
import io.mojaloop.core.transaction.intercom.client.TransactionIntercomClientConfiguration;
import io.mojaloop.core.transaction.producer.TransactionProducerConfiguration;
import io.mojaloop.core.transfer.contract.component.interledger.PartyUnwrapper;
import io.mojaloop.core.wallet.store.WalletStoreConfiguration;
import io.mojaloop.fspiop.common.FspiopCommonConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan(basePackages = {"io.mojaloop.core.transfer.domain"})
@Import(value = {MiscConfiguration.class,
                 FspiopCommonConfiguration.class,
                 TransactionIntercomClientConfiguration.class,
                 TransactionProducerConfiguration.class,
                 ParticipantStoreConfiguration.class,
                 WalletStoreConfiguration.class,
                 RoutingJpaConfiguration.class})
public class TransferDomainConfiguration {

    public interface RequiredBeans extends MiscConfiguration.RequiredBeans,
                                           FspiopCommonConfiguration.RequiredBeans,
                                           TransactionIntercomClientConfiguration.RequiredBeans,
                                           TransactionProducerConfiguration.RequiredBeans,
                                           ParticipantStoreConfiguration.RequiredBeans,
                                           WalletStoreConfiguration.RequiredBeans,
                                           RoutingJpaConfiguration.RequiredBeans {

        PartyUnwrapper partyUnwrapper();

    }

    public interface RequiredSettings extends MiscConfiguration.RequiredSettings,
                                              FspiopCommonConfiguration.RequiredSettings,
                                              TransactionProducerConfiguration.RequiredSettings,
                                              TransactionIntercomClientConfiguration.RequiredSettings,
                                              ParticipantStoreConfiguration.RequiredSettings,
                                              WalletStoreConfiguration.RequiredSettings,
                                              RoutingJpaConfiguration.RequiredSettings {

        TransferSettings transferSettings();

    }

    public record TransferSettings(int reservationTimeoutMs, int expiryTimeoutMs) { }

}
