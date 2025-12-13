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

package org.mojave.core.quoting.service;

import org.mojave.component.jpa.routing.RoutingDataSourceConfigurer;
import org.mojave.component.jpa.routing.RoutingEntityManagerConfigurer;
import org.mojave.component.web.spring.security.SpringSecurityConfigurer;
import org.mojave.core.participant.intercom.client.service.ParticipantIntercomService;
import org.mojave.core.quoting.domain.QuotingDomainConfiguration;
import org.mojave.fspiop.component.FspiopComponentConfiguration;
import org.mojave.fspiop.service.FspiopServiceConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;

final class QuotingServiceSettings implements QuotingServiceConfiguration.RequiredSettings {

    @Bean
    @Override
    public ParticipantIntercomService.Settings participantIntercomServiceSettings() {

        return new ParticipantIntercomService.Settings(
            System.getenv("PARTICIPANT_INTERCOM_BASE_URL"));
    }

    @Bean
    @Override
    public FspiopComponentConfiguration.ParticipantSettings participantSettings() {

        var fspCode = System.getenv("FSPIOP_FSP_CODE");
        var fspName = System.getenv("FSPIOP_FSP_NAME");
        var ilpSecret = System.getenv("FSPIOP_ILP_SECRET");
        var signJws = Boolean.parseBoolean(System.getenv("FSPIOP_SIGN_JWS"));
        var verifyJws = Boolean.parseBoolean(System.getenv("FSPIOP_VERIFY_JWS"));
        var privateKeyPem = System.getenv("FSPIOP_PRIVATE_KEY_PEM");
        var fsps = System.getenv("FSPIOP_FSPS").split(",", -1);
        var fspPublicKeyPem = new HashMap<String, String>();

        for (var fsp : fsps) {

            var env = "FSPIOP_PUBLIC_KEY_PEM_OF_" + fsp.toUpperCase();
            var publicKeyPem = System.getenv(env);

            if (publicKeyPem != null) {
                fspPublicKeyPem.put(fsp, publicKeyPem);
            }
        }

        return new FspiopComponentConfiguration.ParticipantSettings(
            fspCode, fspName, ilpSecret, signJws, verifyJws, privateKeyPem, fspPublicKeyPem);

    }

    @Bean
    @Override
    public QuotingDomainConfiguration.QuoteSettings quoteSettings() {

        return new QuotingDomainConfiguration.QuoteSettings(
            Boolean.parseBoolean(System.getenv("QUOTING_STATEFUL")));
    }

    @Bean
    @Override
    public QuotingServiceConfiguration.TomcatSettings quotingServiceTomcatSettings() {

        return new QuotingServiceConfiguration.TomcatSettings(
            Integer.parseInt(System.getenv("QUOTING_SERVICE_PORT")));
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.ReadSettings routingDataSourceReadSettings() {

        var connection = new RoutingDataSourceConfigurer.ReadSettings.Connection(
            System.getenv("QOT_READ_DB_URL"), System.getenv("QOT_READ_DB_USER"),
            System.getenv("QOT_READ_DB_PASSWORD"), false);

        var pool = new RoutingDataSourceConfigurer.ReadSettings.Pool(
            "quoting-service-read", Integer.parseInt(System.getenv("QOT_READ_DB_MIN_POOL_SIZE")),
            Integer.parseInt(System.getenv("QOT_READ_DB_MAX_POOL_SIZE")));

        return new RoutingDataSourceConfigurer.ReadSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.WriteSettings routingDataSourceWriteSettings() {

        var connection = new RoutingDataSourceConfigurer.WriteSettings.Connection(
            System.getenv("QOT_WRITE_DB_URL"), System.getenv("QOT_WRITE_DB_USER"),
            System.getenv("QOT_WRITE_DB_PASSWORD"), false);

        var pool = new RoutingDataSourceConfigurer.WriteSettings.Pool(
            "quoting-service-write", Integer.parseInt(System.getenv("QOT_WRITE_DB_MIN_POOL_SIZE")),
            Integer.parseInt(System.getenv("QOT_WRITE_DB_MAX_POOL_SIZE")));

        return new RoutingDataSourceConfigurer.WriteSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingEntityManagerConfigurer.Settings routingEntityManagerSettings() {

        return new RoutingEntityManagerConfigurer.Settings("quoting-service", false, false);
    }

    @Bean
    @Override
    public FspiopServiceConfiguration.ServiceSettings serviceSettings() {

        return new FspiopServiceConfiguration.ServiceSettings(
            Integer.parseInt(System.getenv("FSPIOP_SERVICE_REQUEST_AGE_MS")),
            Boolean.parseBoolean(System.getenv("FSPIOP_SERVICE_REQUEST_AGE_VERIFICATION")));
    }

    @Bean
    @Override
    public SpringSecurityConfigurer.Settings springSecuritySettings() {

        return new SpringSecurityConfigurer.Settings(new String[]{"/quotes/**"});
    }

}
