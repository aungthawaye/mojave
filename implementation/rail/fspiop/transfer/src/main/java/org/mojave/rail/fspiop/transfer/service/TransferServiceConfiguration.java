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
package org.mojave.rail.fspiop.transfer.service;

import org.mojave.component.web.logging.RequestIdMdcConfiguration;
import org.mojave.core.participant.intercom.client.ParticipantIntercomClientConfiguration;
import org.mojave.rail.fspiop.transfer.domain.TransferDomainConfiguration;
import org.mojave.core.wallet.intercom.client.WalletIntercomClientConfiguration;
import org.mojave.rail.fspiop.bootstrap.FspiopServiceConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@EnableAsync
@ComponentScan(basePackages = "org.mojave.rail.fspiop.transfer.service.controller")
@Import(
    value = {
        TransferDomainConfiguration.class,
        RequestIdMdcConfiguration.class,
        ParticipantIntercomClientConfiguration.class,
        WalletIntercomClientConfiguration.class,
        FspiopServiceConfiguration.class})
public final class TransferServiceConfiguration {

    public interface RequiredDependencies extends TransferDomainConfiguration.RequiredBeans,
                                                  FspiopServiceConfiguration.RequiredBeans { }

    public interface RequiredSettings extends TransferDomainConfiguration.RequiredSettings,
                                              ParticipantIntercomClientConfiguration.RequiredSettings,
                                              WalletIntercomClientConfiguration.RequiredSettings,
                                              FspiopServiceConfiguration.RequiredSettings {

        TomcatSettings transferServiceTomcatSettings();

    }

    public record TomcatSettings(int portNo) { }

}
