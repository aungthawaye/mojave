package io.mojaloop.core.wallet.domain;

import io.mojaloop.component.flyway.FlywayMigration;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;

public class WalletFlywayConfiguration {

    @Bean(initMethod = "migrate")
    public Flyway walletFlyway(FlywayMigration.Settings walletFlywaySettings) {

        return FlywayMigration.configure(walletFlywaySettings);
    }

    public interface RequiredSettings {

        FlywayMigration.Settings walletFlywaySettings();

    }

}
