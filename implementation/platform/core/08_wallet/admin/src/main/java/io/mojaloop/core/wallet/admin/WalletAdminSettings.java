package io.mojaloop.core.wallet.admin;

import io.mojaloop.component.jpa.routing.RoutingDataSourceConfigurer;
import io.mojaloop.component.jpa.routing.RoutingEntityManagerConfigurer;
import org.springframework.context.annotation.Bean;

public class WalletAdminSettings implements WalletAdminConfiguration.RequiredSettings {

    @Bean
    @Override
    public RoutingDataSourceConfigurer.ReadSettings routingDataSourceReadSettings() {

        var connection = new RoutingDataSourceConfigurer.ReadSettings.Connection(System.getenv().getOrDefault("WLT_READ_DB_URL",
                                                                                                              "jdbc:mysql://localhost:3306/ml_wallet?createDatabaseIfNotExist=true"),
                                                                                 System.getenv().getOrDefault("WLT_READ_DB_USER", "root"),
                                                                                 System.getenv().getOrDefault("WLT_READ_DB_PASSWORD", "password"),
                                                                                 false);

        var pool = new RoutingDataSourceConfigurer.ReadSettings.Pool("wallet-admin-read",
                                                                     Integer.parseInt(System.getenv().getOrDefault("WLT_READ_DB_MIN_POOL_SIZE", "2")),
                                                                     Integer.parseInt(System.getenv().getOrDefault("WLT_READ_DB_MAX_POOL_SIZE", "10")));

        return new RoutingDataSourceConfigurer.ReadSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.WriteSettings routingDataSourceWriteSettings() {

        var connection = new RoutingDataSourceConfigurer.WriteSettings.Connection(System.getenv().getOrDefault("WLT_WRITE_DB_URL",
                                                                                                               "jdbc:mysql://localhost:3306/ml_wallet?createDatabaseIfNotExist=true"),
                                                                                  System.getenv().getOrDefault("WLT_WRITE_DB_USER", "root"),
                                                                                  System.getenv().getOrDefault("WLT_WRITE_DB_PASSWORD", "password"),
                                                                                  false);

        var pool = new RoutingDataSourceConfigurer.WriteSettings.Pool("wallet-admin-write",
                                                                      Integer.parseInt(System.getenv().getOrDefault("WLT_WRITE_DB_MIN_POOL_SIZE", "2")),
                                                                      Integer.parseInt(System.getenv().getOrDefault("WLT_WRITE_DB_MAX_POOL_SIZE", "10")));

        return new RoutingDataSourceConfigurer.WriteSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingEntityManagerConfigurer.Settings routingEntityManagerSettings() {

        return new RoutingEntityManagerConfigurer.Settings("wallet-admin", false, false);
    }

    @Bean
    @Override
    public WalletAdminConfiguration.TomcatSettings tomcatSettings() {

        return new WalletAdminConfiguration.TomcatSettings(Integer.parseInt(System.getenv().getOrDefault("WALLET_ADMIN_PORT", "4901")));
    }

}
