/*-
 * ================================================================================
 * Mojaloop OSS
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

import io.mojaloop.component.misc.ComponentMiscConfiguration;
import io.mojaloop.core.common.datatype.type.fspiop.FspCode;
import io.mojaloop.core.lookup.domain.LookUpDomainConfiguration;
import io.mojaloop.core.participant.utility.store.ParticipantStore;
import io.mojaloop.fspiop.service.FspiopServiceConfiguration;
import io.mojaloop.fspiop.service.component.ParticipantVerifier;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@EnableWebMvc
@EnableAsync
@ComponentScan(basePackages = "io.mojaloop.core.lookup.service")
@Import(value = {
    LookUpDomainConfiguration.class, ComponentMiscConfiguration.class, FspiopServiceConfiguration.class})
public class LookUpServiceConfiguration implements FspiopServiceConfiguration.RequiredBeans, ComponentMiscConfiguration.RequiredBeans {

    private final ParticipantStore participantStore;

    public LookUpServiceConfiguration(ParticipantStore participantStore) {

        assert participantStore != null;

        this.participantStore = participantStore;
    }

    @Bean
    @Override
    public ParticipantVerifier participantVerifier() {

        return fspCode -> this.participantStore.getFspData(new FspCode(fspCode)) != null;
    }

    @Bean
    public Executor taskExecutor() {

        return Executors.newCachedThreadPool();
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer(TomcatSettings settings) {

        return factory -> factory.setPort(settings.portNo());
    }

    public interface RequiredSettings extends LookUpDomainConfiguration.RequiredSettings,
                                              FspiopServiceConfiguration.RequiredSettings,
                                              ComponentMiscConfiguration.RequiredSettings {

        TomcatSettings lookUpServiceTomcatSettings();

    }

    public record TomcatSettings(int portNo) { }

}
