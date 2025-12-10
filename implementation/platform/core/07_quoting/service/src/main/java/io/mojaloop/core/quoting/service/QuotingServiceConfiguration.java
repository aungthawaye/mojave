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

package io.mojaloop.core.quoting.service;

import io.mojaloop.component.web.logging.RequestIdMdcConfiguration;
import io.mojaloop.core.participant.intercom.client.ParticipantIntercomClientConfiguration;
import io.mojaloop.core.quoting.domain.QuotingDomainConfiguration;
import io.mojaloop.fspiop.service.FspiopServiceConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@EnableAsync
@ComponentScan(basePackages = "io.mojaloop.core.quoting.service.controller")
@Import(
    value = {
        QuotingDomainConfiguration.class,
        RequestIdMdcConfiguration.class,
        ParticipantIntercomClientConfiguration.class,
        FspiopServiceConfiguration.class})
public final class QuotingServiceConfiguration {

    public interface RequiredDependencies extends QuotingDomainConfiguration.RequiredBeans,
                                                  FspiopServiceConfiguration.RequiredBeans { }

    public interface RequiredSettings extends QuotingDomainConfiguration.RequiredSettings,
                                              ParticipantIntercomClientConfiguration.RequiredSettings,
                                              FspiopServiceConfiguration.RequiredSettings {

        TomcatSettings quotingServiceTomcatSettings();

    }

    public record TomcatSettings(int portNo) { }

}
