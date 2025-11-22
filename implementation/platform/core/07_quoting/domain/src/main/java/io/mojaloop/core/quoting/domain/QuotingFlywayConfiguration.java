package io.mojaloop.core.quoting.domain;

import io.mojaloop.component.flyway.FlywayMigration;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;

public class QuotingFlywayConfiguration {

    @Bean(initMethod = "migrate")
    public Flyway quotingFlyway(FlywayMigration.Settings quotingFlywaySettings) {

        return FlywayMigration.configure(quotingFlywaySettings);
    }

    public interface RequiredSettings {

        FlywayMigration.Settings quotingFlywaySettings();

    }

}
