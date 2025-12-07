package io.mojaloop.mono.consumer;

import io.mojaloop.component.jpa.routing.RoutingDataSourceConfigurer;
import io.mojaloop.component.jpa.routing.RoutingEntityManagerConfigurer;
import io.mojaloop.core.accounting.consumer.listener.PostLedgerFlowListener;
import io.mojaloop.core.accounting.domain.AccountingFlywayConfiguration;
import io.mojaloop.core.transaction.consumer.listener.AddStepListener;
import io.mojaloop.core.transaction.consumer.listener.CloseTransactionListener;
import io.mojaloop.core.transaction.domain.TransactionFlywayConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.UUID;

public class MonoConsumerSettings implements MonoConsumerConfiguration.RequiredSettings {

    @Bean
    @Override
    public AccountingFlywayConfiguration.Settings accountingFlywaySettings() {

        return new AccountingFlywayConfiguration.Settings(
            System.getenv("MONO_FLYWAY_DB_URL"), System.getenv("MONO_FLYWAY_DB_USER"),
            System.getenv("MONO_FLYWAY_DB_PASSWORD"));
    }

    @Bean
    @Override
    public AddStepListener.Settings addStepListenerSettings() {

        return new AddStepListener.Settings(
            System.getenv("KAFKA_BROKER_URL"), AddStepListener.GROUP_ID,
            UUID.randomUUID().toString(), "earliest", 1, 100, false,
            ContainerProperties.AckMode.MANUAL);
    }

    @Bean
    @Override
    public CloseTransactionListener.Settings closeTransactionListenerSettings() {

        return new CloseTransactionListener.Settings(
            System.getenv("KAFKA_BROKER_URL"), CloseTransactionListener.GROUP_ID,
            UUID.randomUUID().toString(), "earliest", 1, 100, false,
            ContainerProperties.AckMode.MANUAL);
    }

    @Bean
    @Override
    public PostLedgerFlowListener.Settings postLedgerFlowListenerSettings() {

        return new PostLedgerFlowListener.Settings(
            System.getenv("KAFKA_BROKER_URL"), PostLedgerFlowListener.GROUP_ID,
            UUID.randomUUID().toString(), "earliest", 1, 100, false,
            ContainerProperties.AckMode.MANUAL);
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.ReadSettings routingDataSourceReadSettings() {

        var connection = new RoutingDataSourceConfigurer.ReadSettings.Connection(
            System.getenv("MONO_READ_DB_URL"), System.getenv("MONO_READ_DB_USER"),
            System.getenv("MONO_READ_DB_PASSWORD"), false);

        var pool = new RoutingDataSourceConfigurer.ReadSettings.Pool(
            "transaction-consumer-read",
            Integer.parseInt(System.getenv("MONO_READ_DB_MIN_POOL_SIZE")),
            Integer.parseInt(System.getenv("MONO_READ_DB_MAX_POOL_SIZE")));

        return new RoutingDataSourceConfigurer.ReadSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.WriteSettings routingDataSourceWriteSettings() {

        var connection = new RoutingDataSourceConfigurer.WriteSettings.Connection(
            System.getenv("MONO_WRITE_DB_URL"), System.getenv("MONO_WRITE_DB_USER"),
            System.getenv("MONO_WRITE_DB_PASSWORD"), false);

        var pool = new RoutingDataSourceConfigurer.WriteSettings.Pool(
            "transaction-consumer-write",
            Integer.parseInt(System.getenv("MONO_WRITE_DB_MIN_POOL_SIZE")),
            Integer.parseInt(System.getenv("MONO_WRITE_DB_MAX_POOL_SIZE")));

        return new RoutingDataSourceConfigurer.WriteSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingEntityManagerConfigurer.Settings routingEntityManagerSettings() {

        return new RoutingEntityManagerConfigurer.Settings("transaction-consumer", false, false);
    }

    @Bean
    @Override
    public TransactionFlywayConfiguration.Settings transactionFlywaySettings() {

        return new TransactionFlywayConfiguration.Settings(
            System.getenv("MONO_FLYWAY_DB_URL"), System.getenv("MONO_FLYWAY_DB_USER"),
            System.getenv("MONO_FLYWAY_DB_PASSWORD"));
    }

}
