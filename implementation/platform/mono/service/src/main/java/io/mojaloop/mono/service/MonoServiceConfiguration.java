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

package io.mojaloop.mono.service;

import tools.jackson.databind.ObjectMapper;
import io.mojaloop.component.openapi.OpenApiConfiguration;
import io.mojaloop.component.web.error.RestErrorConfiguration;
import io.mojaloop.component.web.logging.RequestIdMdcConfiguration;
import io.mojaloop.component.web.spring.mvc.WebMvcExtension;
import io.mojaloop.core.common.datatype.DatatypeConfiguration;
import io.mojaloop.core.lookup.domain.LookUpDomainConfiguration;
import io.mojaloop.core.lookup.service.LookUpServiceConfiguration;
import io.mojaloop.core.participant.contract.query.FspQuery;
import io.mojaloop.core.participant.contract.query.OracleQuery;
import io.mojaloop.core.participant.intercom.client.ParticipantIntercomClientConfiguration;
import io.mojaloop.core.quoting.domain.QuotingDomainConfiguration;
import io.mojaloop.core.quoting.service.QuotingServiceConfiguration;
import io.mojaloop.core.transaction.intercom.client.TransactionIntercomClientConfiguration;
import io.mojaloop.core.transfer.TransferDomainConfiguration;
import io.mojaloop.core.wallet.intercom.client.WalletIntercomClientConfiguration;
import io.mojaloop.fspiop.service.FspiopServiceConfiguration;
import io.mojaloop.platform.core.transfer.service.TransferServiceConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@EnableAsync
@ComponentScan(
    basePackages = {
        "io.mojaloop.mono.service.controller"})
@Import(
    value = {
        OpenApiConfiguration.class,
        DatatypeConfiguration.class,
        RequestIdMdcConfiguration.class,
        RestErrorConfiguration.class,
        FspiopServiceConfiguration.class,
        LookUpServiceConfiguration.class,
        QuotingServiceConfiguration.class,
        TransferServiceConfiguration.class})
public final class MonoServiceConfiguration extends WebMvcExtension {

    public MonoServiceConfiguration(ObjectMapper objectMapper,
                                    FspQuery fspQuery,
                                    OracleQuery oracleQuery) {

        super(objectMapper);

    }

    public interface RequiredDependencies extends FspiopServiceConfiguration.RequiredBeans,
                                                  LookUpServiceConfiguration.RequiredDependencies,
                                                  QuotingServiceConfiguration.RequiredDependencies,
                                                  TransferServiceConfiguration.RequiredDependencies { }

    public interface RequiredSettings extends OpenApiConfiguration.RequiredSettings,
                                              LookUpDomainConfiguration.RequiredSettings,
                                              QuotingDomainConfiguration.RequiredSettings,
                                              TransferDomainConfiguration.RequiredSettings,
                                              FspiopServiceConfiguration.RequiredSettings,
                                              ParticipantIntercomClientConfiguration.RequiredSettings,
                                              WalletIntercomClientConfiguration.RequiredSettings,
                                              TransactionIntercomClientConfiguration.RequiredSettings {

        TomcatSettings tomcatSettings();

    }

    public record TomcatSettings(int portNo) { }

}
