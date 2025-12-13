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

package org.mojave.platform.core.transfer.service;

import org.mojave.component.jpa.routing.RoutingDataSourceConfigurer;
import org.mojave.component.jpa.routing.RoutingEntityManagerConfigurer;
import org.mojave.component.web.spring.security.SpringSecurityConfigurer;
import org.mojave.core.accounting.producer.AccountingProducerConfiguration;
import org.mojave.core.participant.intercom.client.service.ParticipantIntercomService;
import org.mojave.core.transaction.intercom.client.service.TransactionIntercomService;
import org.mojave.core.transaction.producer.TransactionProducerConfiguration;
import org.mojave.core.transfer.TransferDomainConfiguration;
import org.mojave.core.wallet.intercom.client.service.WalletIntercomService;
import org.mojave.fspiop.component.FspiopComponentConfiguration;
import org.mojave.fspiop.service.FspiopServiceConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;

final class TransferServiceSettings implements TransferServiceConfiguration.RequiredSettings {

    @Bean
    @Override
    public AccountingProducerConfiguration.ProducerSettings accountingProducerSettings() {

        return new AccountingProducerConfiguration.ProducerSettings(
            System.getenv("KAFKA_BOOTSTRAP_SERVERS"), "all");
    }

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
        var fspsEnv = System.getenv("FSPIOP_FSPS");
        var fspPublicKeyPem = new HashMap<String, String>();

        if (fspsEnv != null && !fspsEnv.isEmpty()) {
            var fsps = fspsEnv.split(",", -1);
            for (var fsp : fsps) {
                var env = "FSPIOP_PUBLIC_KEY_PEM_OF_" + fsp.toUpperCase();
                var publicKeyPem = System.getenv(env);
                if (publicKeyPem != null) {
                    fspPublicKeyPem.put(fsp, publicKeyPem);
                }
            }
        }

        return new FspiopComponentConfiguration.ParticipantSettings(
            fspCode, fspName, ilpSecret, signJws, verifyJws, privateKeyPem, fspPublicKeyPem);
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.ReadSettings routingDataSourceReadSettings() {

        var connection = new RoutingDataSourceConfigurer.ReadSettings.Connection(
            System.getenv("TFR_READ_DB_URL"), System.getenv("TFR_READ_DB_USER"),
            System.getenv("TFR_READ_DB_PASSWORD"), false);

        var pool = new RoutingDataSourceConfigurer.ReadSettings.Pool(
            "transfer-service-read", Integer.parseInt(System.getenv("TFR_READ_DB_MIN_POOL_SIZE")),
            Integer.parseInt(System.getenv("TFR_READ_DB_MAX_POOL_SIZE")));

        return new RoutingDataSourceConfigurer.ReadSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.WriteSettings routingDataSourceWriteSettings() {

        var connection = new RoutingDataSourceConfigurer.WriteSettings.Connection(
            System.getenv("TFR_WRITE_DB_URL"), System.getenv("TFR_WRITE_DB_USER"),
            System.getenv("TFR_WRITE_DB_PASSWORD"), false);

        var pool = new RoutingDataSourceConfigurer.WriteSettings.Pool(
            "transfer-service-write", Integer.parseInt(System.getenv("TFR_WRITE_DB_MIN_POOL_SIZE")),
            Integer.parseInt(System.getenv("TFR_WRITE_DB_MAX_POOL_SIZE")));

        return new RoutingDataSourceConfigurer.WriteSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingEntityManagerConfigurer.Settings routingEntityManagerSettings() {

        return new RoutingEntityManagerConfigurer.Settings("transfer-service", false, false);
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

        return new SpringSecurityConfigurer.Settings(new String[]{"/transfers/**"});
    }

    @Bean
    @Override
    public TransactionIntercomService.Settings transactionIntercomServiceSettings() {

        return new TransactionIntercomService.Settings(
            System.getenv("TRANSACTION_INTERCOM_BASE_URL"));
    }

    @Bean
    @Override
    public TransactionProducerConfiguration.ProducerSettings transactionProducerSettings() {

        return new TransactionProducerConfiguration.ProducerSettings(
            System.getenv("KAFKA_BOOTSTRAP_SERVERS"), "all");
    }

    @Bean
    @Override
    public TransferServiceConfiguration.TomcatSettings transferServiceTomcatSettings() {

        return new TransferServiceConfiguration.TomcatSettings(
            Integer.parseInt(System.getenv("TRANSFER_SERVICE_PORT")));
    }

    @Bean
    @Override
    public TransferDomainConfiguration.TransferSettings transferSettings() {

        return new TransferDomainConfiguration.TransferSettings(
            Integer.parseInt(System.getenv("TRANSFER_RESERVATION_TIMEOUT_MS")),
            Integer.parseInt(System.getenv("TRANSFER_EXPIRY_TIMEOUT_MS")));
    }

    @Bean
    @Override
    public WalletIntercomService.Settings walletIntercomServiceSettings() {

        return new WalletIntercomService.Settings(System.getenv("WALLET_INTERCOM_BASE_URL"));

    }

}
