package io.mojaloop.core.quoting.domain;

import io.mojaloop.component.flyway.FlywayMigration;
import org.springframework.context.annotation.Import;

@Import(value = {QuotingDomainConfiguration.class, TestSettings.class, MocksConfiguration.class})
public class TestConfiguration {

    static {

        var flywaySettings = new FlywayMigration.Settings(
            "jdbc:mysql://localhost:3306/ml_quoting?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true",
            "root", "password", "classpath:migration/quoting");

        FlywayMigration.migrate(flywaySettings);
    }
}
