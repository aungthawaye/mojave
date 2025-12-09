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

package io.mojaloop.core.lookup.service;

import io.mojaloop.component.web.logging.RequestIdMdcConfiguration;
import io.mojaloop.core.lookup.domain.LookUpDomainConfiguration;
import io.mojaloop.core.participant.intercom.client.ParticipantIntercomClientConfiguration;
import io.mojaloop.fspiop.service.FspiopServiceConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})

@EnableWebMvc
@EnableAsync
@ComponentScan(basePackages = "io.mojaloop.core.lookup.service.controller")
@Import(
    value = {
        LookUpDomainConfiguration.class,
        RequestIdMdcConfiguration.class,
        ParticipantIntercomClientConfiguration.class,
        FspiopServiceConfiguration.class})
public final class LookUpServiceConfiguration {

    public interface RequiredDependencies extends LookUpDomainConfiguration.RequiredBeans,
                                                  RequestIdMdcConfiguration.RequiredBeans,
                                                  ParticipantIntercomClientConfiguration.RequiredBeans,
                                                  FspiopServiceConfiguration.RequiredBeans { }

    public interface RequiredSettings extends LookUpDomainConfiguration.RequiredSettings,
                                              RequestIdMdcConfiguration.RequiredSettings,
                                              ParticipantIntercomClientConfiguration.RequiredSettings,
                                              FspiopServiceConfiguration.RequiredSettings {

        TomcatSettings lookUpServiceTomcatSettings();

    }

    public record TomcatSettings(int portNo) { }

}
