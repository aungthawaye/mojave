package io.mojaloop.core.account.domain;

import io.mojaloop.component.jpa.routing.RoutingDataSourceConfigurer;
import io.mojaloop.component.jpa.routing.RoutingEntityManagerConfigurer;
import io.mojaloop.component.redis.RedisDefaults;
import io.mojaloop.component.redis.RedissonOpsClientConfigurer;
import io.mojaloop.core.account.domain.component.ledger.strategy.MySqlLedger;
import org.springframework.context.annotation.Bean;

public class TestSettings implements AccountDomainConfiguration.RequiredSettings {

    @Bean
    @Override
    public MySqlLedger.LedgerDbSettings ledgerDbSettings() {

        return new MySqlLedger.LedgerDbSettings(new MySqlLedger.LedgerDbSettings.Connection(
            "jdbc:mysql://localhost:3306/ml_account?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true", "root",
            "password", false), new MySqlLedger.LedgerDbSettings.Pool("account-ledger", 2, 10));
    }

    @Bean
    @Override
    public RedissonOpsClientConfigurer.Settings redissonOpsClientSettings() {

        return new RedissonOpsClientConfigurer.Settings(new String[]{"redis://localhost:6379"}, false, RedisDefaults.CODEC, RedisDefaults.EXECUTOR_COUNT,
                                                        RedisDefaults.CONNECTION_POOL_SIZE, RedisDefaults.CONNECTION_POOL_MIN_IDLE);
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.ReadSettings routingDataSourceReadSettings() {

        return new RoutingDataSourceConfigurer.ReadSettings(new RoutingDataSourceConfigurer.ReadSettings.Connection(
            "jdbc:mysql://localhost:3306/ml_account?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true", "root",
            "password", false), new RoutingDataSourceConfigurer.ReadSettings.Pool("account-read", 2, 4));
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.WriteSettings routingDataSourceWriteSettings() {

        return new RoutingDataSourceConfigurer.WriteSettings(new RoutingDataSourceConfigurer.WriteSettings.Connection(
            "jdbc:mysql://localhost:3306/ml_account?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true", "root",
            "password", false), new RoutingDataSourceConfigurer.WriteSettings.Pool("account-write", 2, 4));
    }

    @Bean
    @Override
    public RoutingEntityManagerConfigurer.Settings routingEntityManagerSettings() {

        return new RoutingEntityManagerConfigurer.Settings("account-domain", false, false);
    }

}
