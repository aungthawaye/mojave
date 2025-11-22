package io.mojaloop.core.transfer;

import io.mojaloop.component.flyway.FlywayMigration;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;

public class TransferFlywayConfiguration {

    @Bean(initMethod = "migrate")
    public Flyway transferFlyway(FlywayMigration.Settings transferFlywaySettings) {

        return FlywayMigration.configure(transferFlywaySettings);
    }

    public interface RequiredSettings {

        FlywayMigration.Settings transferFlywaySettings();

    }

}
