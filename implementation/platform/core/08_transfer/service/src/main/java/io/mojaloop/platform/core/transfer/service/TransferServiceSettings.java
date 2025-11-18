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

import io.mojaloop.component.jpa.routing.RoutingDataSourceConfigurer;
import io.mojaloop.component.jpa.routing.RoutingEntityManagerConfigurer;
import io.mojaloop.component.kafka.KafkaProducerConfigurer;
import io.mojaloop.component.web.spring.security.SpringSecurityConfigurer;
import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.core.participant.intercom.client.service.ParticipantIntercomService;
import io.mojaloop.core.participant.store.ParticipantStoreConfiguration;
import io.mojaloop.core.transaction.intercom.client.service.TransactionIntercomService;
import io.mojaloop.core.transfer.TransferDomainConfiguration;
import io.mojaloop.core.wallet.intercom.client.service.WalletIntercomService;
import io.mojaloop.core.wallet.store.WalletStoreConfiguration;
import io.mojaloop.fspiop.common.FspiopCommonConfiguration;
import io.mojaloop.fspiop.service.FspiopServiceConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class TransferServiceSettings implements TransferServiceConfiguration.RequiredSettings {

    @Bean
    @Override
    public TransferServiceConfiguration.FspCodeList fspCodeList() {

        var fspsEnv = System.getenv("FSPIOP_FSPS");

        if (fspsEnv != null && !fspsEnv.isEmpty()) {
            return new TransferServiceConfiguration.FspCodeList(Arrays.stream(fspsEnv.split(",")).map(String::trim).map(FspCode::new).collect(Collectors.toList()));
        }

        return new TransferServiceConfiguration.FspCodeList(List.of());
    }

    @Bean
    @Override
    public FspiopCommonConfiguration.ParticipantSettings fspiopCommonParticipantSettings() {

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

        return new FspiopCommonConfiguration.ParticipantSettings(fspCode, fspName, ilpSecret, signJws, verifyJws, privateKeyPem, fspPublicKeyPem);
    }

    @Bean
    @Override
    public ParticipantIntercomService.Settings participantIntercomServiceSettings() {

        return new ParticipantIntercomService.Settings(System.getenv().getOrDefault("PARTICIPANT_INTERCOM_BASE_URL", "http://localhost:4102"));
    }

    @Bean
    @Override
    public ParticipantStoreConfiguration.Settings participantStoreSettings() {

        return new ParticipantStoreConfiguration.Settings(Integer.parseInt(System.getenv().getOrDefault("PARTICIPANT_STORE_REFRESH_INTERVAL_MS", "300000")));
    }

    @Bean
    @Override
    public KafkaProducerConfigurer.ProducerSettings producerSettings() {

        return new KafkaProducerConfigurer.ProducerSettings(System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092"), "all");
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.ReadSettings routingDataSourceReadSettings() {

        var connection = new RoutingDataSourceConfigurer.ReadSettings.Connection(
            System.getenv().getOrDefault("TFR_READ_DB_URL", "jdbc:mysql://localhost:3306/ml_transfer?createDatabaseIfNotExist=true"),
            System.getenv().getOrDefault("TFR_READ_DB_USER", "root"), System.getenv().getOrDefault("TFR_READ_DB_PASSWORD", "password"), false);

        var pool = new RoutingDataSourceConfigurer.ReadSettings.Pool("transfer-service-read", Integer.parseInt(System.getenv().getOrDefault("TFR_READ_DB_MIN_POOL_SIZE", "2")),
            Integer.parseInt(System.getenv().getOrDefault("TFR_READ_DB_MAX_POOL_SIZE", "10")));

        return new RoutingDataSourceConfigurer.ReadSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.WriteSettings routingDataSourceWriteSettings() {

        var connection = new RoutingDataSourceConfigurer.WriteSettings.Connection(
            System.getenv().getOrDefault("TFR_WRITE_DB_URL", "jdbc:mysql://localhost:3306/ml_transfer?createDatabaseIfNotExist=true"),
            System.getenv().getOrDefault("TFR_WRITE_DB_USER", "root"), System.getenv().getOrDefault("TFR_WRITE_DB_PASSWORD", "password"), false);

        var pool = new RoutingDataSourceConfigurer.WriteSettings.Pool("transfer-service-write", Integer.parseInt(System.getenv().getOrDefault("TFR_WRITE_DB_MIN_POOL_SIZE", "2")),
            Integer.parseInt(System.getenv().getOrDefault("TFR_WRITE_DB_MAX_POOL_SIZE", "10")));

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

        return new FspiopServiceConfiguration.ServiceSettings(Integer.parseInt(System.getenv().getOrDefault("FSPIOP_SERVICE_REQUEST_AGE_MS", "30000")),
            Boolean.parseBoolean(System.getenv().getOrDefault("FSPIOP_SERVICE_REQUEST_AGE_VERIFICATION", "true")));
    }

    @Bean
    @Override
    public SpringSecurityConfigurer.Settings springSecuritySettings() {

        return new SpringSecurityConfigurer.Settings(new String[]{"/transfers/**"});
    }

    @Bean
    @Override
    public TransactionIntercomService.Settings transactionIntercomServiceSettings() {

        return new TransactionIntercomService.Settings(System.getenv().getOrDefault("TRANSACTION_INTERCOM_BASE_URL", "http://localhost:4302"));
    }

    @Bean
    @Override
    public TransferServiceConfiguration.TomcatSettings transferServiceTomcatSettings() {

        return new TransferServiceConfiguration.TomcatSettings(Integer.parseInt(System.getenv().getOrDefault("TRANSFER_SERVICE_PORT", "4704")));
    }

    @Bean
    @Override
    public TransferDomainConfiguration.TransferSettings transferSettings() {

        return new TransferDomainConfiguration.TransferSettings(Integer.parseInt(System.getenv().getOrDefault("TRANSFER_RESERVATION_TIMEOUT_MS", "300000")),
            Integer.parseInt(System.getenv().getOrDefault("TRANSFER_EXPIRY_TIMEOUT_MS", "60000")));
    }

    @Bean
    @Override
    public WalletIntercomService.Settings walletIntercomServiceSettings() {

        return new WalletIntercomService.Settings(System.getenv().getOrDefault("WALLET_INTERCOM_BASE_URL", "http://localhost:4402"));

    }

    @Bean
    @Override
    public WalletStoreConfiguration.Settings walletStoreSettings() {

        return new WalletStoreConfiguration.Settings(Integer.parseInt(System.getenv().getOrDefault("WALLET_STORE_REFRESH_INTERVAL_MS", "300000")));
    }

}
