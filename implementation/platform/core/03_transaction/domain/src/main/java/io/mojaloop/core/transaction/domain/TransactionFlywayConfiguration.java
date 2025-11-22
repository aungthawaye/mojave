package io.mojaloop.core.transaction.domain;

import io.mojaloop.component.flyway.FlywayMigration;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;

public class TransactionFlywayConfiguration {

    @Bean(initMethod = "migrate")
    public Flyway transactionFlyway(FlywayMigration.Settings transactionFlywaySettings) {

        return FlywayMigration.configure(transactionFlywaySettings);
    }

    public interface RequiredSettings {

        FlywayMigration.Settings transactionFlywaySettings();

    }

}
