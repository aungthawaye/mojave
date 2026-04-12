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

package org.mojave.mono.core.intercom;

import org.mojave.core.accounting.intercom.replier.AccountingIntercomReplierConfiguration;
import org.mojave.core.participant.intercom.replier.ParticipantIntercomReplierConfiguration;
import org.mojave.core.wallet.intercom.replier.WalletIntercomReplierConfiguration;
import org.mojave.scheme.rule.DatatypeConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@ComponentScan(
    basePackages = {"org.mojave.mono.intercom.controller"})
@Import(
    value = {
        DatatypeConfiguration.class,
        AccountingIntercomReplierConfiguration.class,
        ParticipantIntercomReplierConfiguration.class,
        WalletIntercomReplierConfiguration.class})
public class MonoIntercomConfiguration {

    public MonoIntercomConfiguration() {

    }

    public interface RequiredDependencies
        extends ParticipantIntercomReplierConfiguration.RequiredDependencies,
                WalletIntercomReplierConfiguration.RequiredDependencies,
                AccountingIntercomReplierConfiguration.RequiredDependencies { }

    public interface RequiredSettings
        extends ParticipantIntercomReplierConfiguration.RequiredSettings,
                WalletIntercomReplierConfiguration.RequiredSettings,
                AccountingIntercomReplierConfiguration.RequiredSettings {

    }

}
