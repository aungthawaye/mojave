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

package org.mojave.rail.fspiop.mono.service;

import org.mojave.component.openapi.OpenApiConfiguration;
import org.mojave.component.web.error.RestErrorConfiguration;
import org.mojave.component.web.logging.RequestIdMdcConfiguration;
import org.mojave.component.web.spring.mvc.JsonWebMvcConfigurationSupport;
import org.mojave.core.common.datatype.DatatypeConfiguration;
import org.mojave.core.participant.contract.query.FspQuery;
import org.mojave.core.participant.contract.query.OracleQuery;
import org.mojave.core.participant.intercom.client.ParticipantIntercomClientConfiguration;
import org.mojave.core.wallet.intercom.client.WalletIntercomClientConfiguration;
import org.mojave.rail.fspiop.bootstrap.FspiopServiceConfiguration;
import org.mojave.rail.fspiop.lookup.domain.LookUpDomainConfiguration;
import org.mojave.rail.fspiop.lookup.service.LookUpServiceConfiguration;
import org.mojave.rail.fspiop.quoting.domain.QuotingDomainConfiguration;
import org.mojave.rail.fspiop.quoting.service.QuotingServiceConfiguration;
import org.mojave.rail.fspiop.transfer.domain.TransferDomainConfiguration;
import org.mojave.rail.fspiop.transfer.service.TransferServiceConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import tools.jackson.databind.ObjectMapper;

@EnableWebMvc
@EnableAsync
@ComponentScan(
    basePackages = {
        "org.mojave.mono.service.controller"})
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
public final class MonoServiceConfiguration extends JsonWebMvcConfigurationSupport {

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
                                              WalletIntercomClientConfiguration.RequiredSettings {

        TomcatSettings tomcatSettings();

    }

    public record TomcatSettings(int portNo) { }

}
