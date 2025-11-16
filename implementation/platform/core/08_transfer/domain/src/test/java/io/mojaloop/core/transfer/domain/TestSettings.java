package io.mojaloop.core.transfer.domain;

import io.mojaloop.component.jpa.routing.RoutingDataSourceConfigurer;
import io.mojaloop.component.jpa.routing.RoutingEntityManagerConfigurer;
import io.mojaloop.component.kafka.KafkaProducerConfigurer;
import io.mojaloop.core.participant.intercom.client.service.ParticipantIntercomService;
import io.mojaloop.core.participant.store.ParticipantStoreConfiguration;
import io.mojaloop.core.transaction.intercom.client.service.TransactionIntercomService;
import io.mojaloop.core.transfer.TransferDomainConfiguration;
import io.mojaloop.core.wallet.intercom.client.service.WalletIntercomService;
import io.mojaloop.core.wallet.store.WalletStoreConfiguration;
import io.mojaloop.fspiop.common.FspiopCommonConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Map;

public class TestSettings implements TransferDomainConfiguration.RequiredSettings {

    @Bean
    @Override
    public FspiopCommonConfiguration.ParticipantSettings fspiopCommonParticipantSettings() {

        return new FspiopCommonConfiguration.ParticipantSettings("hub", "Hub", "53cr3t", true, true, "dummy", Map.of());
    }

    @Bean
    @Override
    public ParticipantIntercomService.Settings participantIntercomServiceSettings() {

        return new ParticipantIntercomService.Settings("http://localhost:4102");
    }

    @Bean
    @Override
    public ParticipantStoreConfiguration.Settings participantStoreSettings() {

        return new ParticipantStoreConfiguration.Settings(30000);
    }

    @Bean
    @Override
    public KafkaProducerConfigurer.ProducerSettings producerSettings() {

        return new KafkaProducerConfigurer.ProducerSettings("localhost:9092", "all");
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.ReadSettings routingDataSourceReadSettings() {

        return new RoutingDataSourceConfigurer.ReadSettings(
            new RoutingDataSourceConfigurer.ReadSettings.Connection(
                "jdbc:mysql://localhost:3306/ml_transfer?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true", "root", "password", false),
            new RoutingDataSourceConfigurer.ReadSettings.Pool("transfer-read", 2, 4));
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.WriteSettings routingDataSourceWriteSettings() {

        return new RoutingDataSourceConfigurer.WriteSettings(
            new RoutingDataSourceConfigurer.WriteSettings.Connection(
                "jdbc:mysql://localhost:3306/ml_transfer?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true", "root", "password", false),
            new RoutingDataSourceConfigurer.WriteSettings.Pool("transfer-write", 2, 4));
    }

    @Bean
    @Override
    public RoutingEntityManagerConfigurer.Settings routingEntityManagerSettings() {

        return new RoutingEntityManagerConfigurer.Settings("transfer-domain", false, false);
    }

    @Bean
    @Override
    public TransactionIntercomService.Settings transactionIntercomServiceSettings() {

        return new TransactionIntercomService.Settings("http://localhost:4302");
    }

    @Bean
    @Override
    public TransferDomainConfiguration.TransferSettings transferSettings() {

        return new TransferDomainConfiguration.TransferSettings(30000, 30000);
    }

    @Bean
    @Override
    public WalletIntercomService.Settings walletIntercomServiceSettings() {

        return new WalletIntercomService.Settings("http://localhost:4402");
    }

    @Bean
    @Override
    public WalletStoreConfiguration.Settings walletStoreSettings() {

        return new WalletStoreConfiguration.Settings(30000);
    }

}
