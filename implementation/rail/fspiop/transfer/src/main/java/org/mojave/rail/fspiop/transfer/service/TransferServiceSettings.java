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

import org.mojave.component.jpa.routing.RoutingDataSourceConfigurer;
import org.mojave.component.jpa.routing.RoutingEntityManagerConfigurer;
import org.mojave.component.web.spring.security.SpringSecurityConfigurer;
import org.mojave.core.accounting.producer.AccountingProducerConfiguration;
import org.mojave.core.participant.intercom.client.service.ParticipantIntercomService;
import org.mojave.rail.fspiop.transfer.domain.TransferDomainConfiguration;
import org.mojave.rail.fspiop.transfer.domain.TransferKafkaConfiguration;
import org.mojave.rail.fspiop.transfer.domain.kafka.listener.AbortTransferStepListener;
import org.mojave.rail.fspiop.transfer.domain.kafka.listener.CommitTransferStepListener;
import org.mojave.rail.fspiop.transfer.domain.kafka.listener.DisputeTransferStepListener;
import org.mojave.rail.fspiop.transfer.domain.kafka.listener.RollbackReservationStepListener;
import org.mojave.core.wallet.intercom.client.service.WalletIntercomService;
import org.mojave.core.wallet.producer.WalletProducerConfiguration;
import org.mojave.rail.fspiop.component.FspiopComponentConfiguration;
import org.mojave.rail.fspiop.bootstrap.FspiopServiceConfiguration;
import org.mojave.scheme.fspiop.core.Currency;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

final class TransferServiceSettings implements TransferServiceConfiguration.RequiredSettings {

    @Bean
    @Override
    public AbortTransferStepListener.Settings abortTransferStepListenerSettings() {

        return new AbortTransferStepListener.Settings(
            System.getenv("KAFKA_BROKER_URL"), AbortTransferStepListener.GROUP_ID,
            UUID.randomUUID().toString(), "earliest", 100, 1, 1000, false,
            ContainerProperties.AckMode.MANUAL);
    }

    @Bean
    @Override
    public AccountingProducerConfiguration.ProducerSettings accountingProducerSettings() {

        return new AccountingProducerConfiguration.ProducerSettings(
            System.getenv("KAFKA_BOOTSTRAP_SERVERS"), "all");
    }

    @Bean
    @Override
    public CommitTransferStepListener.Settings commitTransferStepListenerSettings() {

        return new CommitTransferStepListener.Settings(
            System.getenv("KAFKA_BROKER_URL"), CommitTransferStepListener.GROUP_ID,
            UUID.randomUUID().toString(), "earliest", 100, 1, 1000, false,
            ContainerProperties.AckMode.MANUAL);
    }

    @Bean
    @Override
    public DisputeTransferStepListener.Settings disputeTransferStepListenerSettings() {

        return new DisputeTransferStepListener.Settings(
            System.getenv("KAFKA_BROKER_URL"), DisputeTransferStepListener.GROUP_ID,
            UUID.randomUUID().toString(), "earliest", 100, 1, 1000, false,
            ContainerProperties.AckMode.MANUAL);
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

        var hubCode = System.getenv("FSPIOP_HUB_CODE");
        var fspCode = System.getenv("FSPIOP_FSP_CODE");
        var fspName = System.getenv("FSPIOP_FSP_NAME");

        var currencyNames = System.getenv("FSPIOP_CURRENCIES").split(",", -1);
        var currencies = new ArrayList<Currency>();

        for (var currencyName : currencyNames) {
            currencies.add(Currency.valueOf(currencyName));
        }

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
            hubCode, fspCode, fspName,
            currencies, ilpSecret, signJws, verifyJws, privateKeyPem, fspPublicKeyPem);
    }

    @Bean
    @Override
    public RollbackReservationStepListener.Settings rollbackReservationStepListenerSettings() {

        return new RollbackReservationStepListener.Settings(
            System.getenv("KAFKA_BROKER_URL"), RollbackReservationStepListener.GROUP_ID,
            UUID.randomUUID().toString(), "earliest", 100, 1, 1000, false,
            ContainerProperties.AckMode.MANUAL);
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.ReadSettings routingDataSourceReadSettings() {

        var connection = new RoutingDataSourceConfigurer.ReadSettings.Connection(
            System.getenv("READ_DB_URL"), System.getenv("READ_DB_USER"),
            System.getenv("READ_DB_PASSWORD"),
            Long.parseLong(System.getenv("READ_DB_CONNECTION_TIMEOUT")),
            Long.parseLong(System.getenv("READ_DB_VALIDATION_TIMEOUT")),
            Long.parseLong(System.getenv("READ_DB_MAX_LIFETIME_TIMEOUT")),
            Long.parseLong(System.getenv("READ_DB_IDLE_TIMEOUT")),
            Long.parseLong(System.getenv("READ_DB_KEEPALIVE_TIMEOUT")), false);

        var pool = new RoutingDataSourceConfigurer.ReadSettings.Pool(
            "transfer-service-read", Integer.parseInt(System.getenv("READ_DB_MIN_POOL_SIZE")),
            Integer.parseInt(System.getenv("READ_DB_MAX_POOL_SIZE")));

        return new RoutingDataSourceConfigurer.ReadSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.WriteSettings routingDataSourceWriteSettings() {

        var connection = new RoutingDataSourceConfigurer.WriteSettings.Connection(
            System.getenv("WRITE_DB_URL"), System.getenv("WRITE_DB_USER"),
            System.getenv("WRITE_DB_PASSWORD"),
            Long.parseLong(System.getenv("WRITE_DB_CONNECTION_TIMEOUT")),
            Long.parseLong(System.getenv("WRITE_DB_VALIDATION_TIMEOUT")),
            Long.parseLong(System.getenv("WRITE_DB_MAX_LIFETIME_TIMEOUT")),
            Long.parseLong(System.getenv("WRITE_DB_IDLE_TIMEOUT")),
            Long.parseLong(System.getenv("WRITE_DB_KEEPALIVE_TIMEOUT")), false);

        var pool = new RoutingDataSourceConfigurer.WriteSettings.Pool(
            "transfer-service-write", Integer.parseInt(System.getenv("WRITE_DB_MIN_POOL_SIZE")),
            Integer.parseInt(System.getenv("WRITE_DB_MAX_POOL_SIZE")));

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
    public TransferKafkaConfiguration.ProducerSettings transferProducerSettings() {

        return new TransferKafkaConfiguration.ProducerSettings(
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

    @Bean
    @Override
    public WalletProducerConfiguration.ProducerSettings walletProducerSettings() {

        return new WalletProducerConfiguration.ProducerSettings(
            System.getenv("KAFKA_BOOTSTRAP_SERVERS"), "all");
    }

}
