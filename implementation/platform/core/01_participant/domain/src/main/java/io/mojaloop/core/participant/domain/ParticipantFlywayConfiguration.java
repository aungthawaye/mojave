package io.mojaloop.core.participant.domain;

import io.mojaloop.component.flyway.FlywayMigration;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;

public class ParticipantFlywayConfiguration {

    @Bean(initMethod = "migrate")
    public Flyway participantFlyway(FlywayMigration.Settings participantFlywaySettings) {

        return FlywayMigration.configure(participantFlywaySettings);
    }

    public interface RequiredSettings {

        FlywayMigration.Settings participantFlywaySettings();

    }

}
