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

package io.mojaloop.platform.core.transfer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.core.participant.intercom.client.ParticipantIntercomClientConfiguration;
import io.mojaloop.core.participant.store.ParticipantStore;
import io.mojaloop.core.transaction.intercom.client.TransactionIntercomClientConfiguration;
import io.mojaloop.core.transfer.TransferDomainConfiguration;
import io.mojaloop.core.transfer.contract.component.interledger.PartyUnwrapper;
import io.mojaloop.core.transfer.domain.component.interledger.unwrapper.MojavePartyUnwrapper;
import io.mojaloop.core.wallet.intercom.client.WalletIntercomClientConfiguration;
import io.mojaloop.fspiop.service.FspiopServiceConfiguration;
import io.mojaloop.fspiop.service.component.ParticipantVerifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@Configuration(proxyBeanMethods = false)
@EnableWebMvc
@EnableAsync
@ComponentScan(basePackages = "io.mojaloop.platform.core.transfer.service")
@Import(value = {TransferDomainConfiguration.class,
                 ParticipantIntercomClientConfiguration.class,
                 WalletIntercomClientConfiguration.class,
                 TransactionIntercomClientConfiguration.class,
                 FspiopServiceConfiguration.class})
public class TransferServiceConfiguration implements TransferDomainConfiguration.RequiredBeans, FspiopServiceConfiguration.RequiredBeans {

    private final ParticipantStore participantStore;

    private final ObjectMapper objectMapper;

    public TransferServiceConfiguration(ParticipantStore participantStore, FspCodeList fspCodeList, ObjectMapper objectMapper) {

        assert participantStore != null;
        assert fspCodeList != null;
        assert objectMapper != null;

        this.participantStore = participantStore;
        this.objectMapper = objectMapper;
    }

    @Bean
    @Override
    public ParticipantVerifier participantVerifier() {

        return fspCode -> this.participantStore.getFspData(new FspCode(fspCode)) != null;
    }

    @Bean
    @Override
    public PartyUnwrapper partyUnwrapper() {

        return new MojavePartyUnwrapper(this.objectMapper);
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer(TomcatSettings settings) {

        return factory -> factory.setPort(settings.portNo());
    }

    public interface RequiredSettings extends TransferDomainConfiguration.RequiredSettings,
                                              ParticipantIntercomClientConfiguration.RequiredSettings,
                                              WalletIntercomClientConfiguration.RequiredSettings,
                                              TransactionIntercomClientConfiguration.RequiredSettings,
                                              FspiopServiceConfiguration.RequiredSettings {

        FspCodeList fspCodeList();

        TomcatSettings transferServiceTomcatSettings();

    }

    public record TomcatSettings(int portNo) { }

    public record FspCodeList(List<FspCode> fspCodes) { }

}
