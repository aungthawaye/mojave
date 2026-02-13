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

import org.mojave.component.web.logging.RequestIdMdcConfiguration;
import org.mojave.core.participant.intercom.client.ParticipantIntercomClientConfiguration;
import org.mojave.rail.fspiop.bootstrap.FspiopServiceConfiguration;
import org.mojave.rail.fspiop.lookup.domain.LookUpDomainConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@EnableAsync
@ComponentScan(basePackages = "org.mojave.rail.fspiop.lookup.service")
@Import(
    value = {
        LookUpDomainConfiguration.class,
        RequestIdMdcConfiguration.class,
        ParticipantIntercomClientConfiguration.class,
        FspiopServiceConfiguration.class})
public final class LookUpServiceConfiguration {

    public interface RequiredDependencies extends LookUpDomainConfiguration.RequiredDependencies,
                                                  RequestIdMdcConfiguration.RequiredDependencies,
                                                  ParticipantIntercomClientConfiguration.RequiredDependencies,
                                                  FspiopServiceConfiguration.RequiredDependencies { }

    public interface RequiredSettings extends LookUpDomainConfiguration.RequiredSettings,
                                              RequestIdMdcConfiguration.RequiredSettings,
                                              ParticipantIntercomClientConfiguration.RequiredSettings,
                                              FspiopServiceConfiguration.RequiredSettings {

        TomcatSettings lookUpServiceTomcatSettings();

    }

    public record TomcatSettings(int portNo) { }

}
