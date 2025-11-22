package io.mojaloop.core.accounting.domain;

import io.mojaloop.component.flyway.FlywayMigration;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;

public class AccountingFlywayConfiguration {

    @Bean(initMethod = "migrate")
    public Flyway accountingFlyway(FlywayMigration.Settings accountingFlywaySettings) {

        return FlywayMigration.configure(accountingFlywaySettings);
    }

    public interface RequiredSettings {

        FlywayMigration.Settings accountingFlywaySettings();

    }

}
