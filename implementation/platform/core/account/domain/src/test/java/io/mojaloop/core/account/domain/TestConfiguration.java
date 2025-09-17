package io.mojaloop.core.account.domain;

import io.mojaloop.component.flyway.FlywayMigration;
import org.springframework.context.annotation.Import;

@Import(value = {AccountDomainConfiguration.class, TestSettings.class})
public class TestConfiguration {

    static {

        var flywaySettings = new FlywayMigration.Settings(
            "jdbc:mysql://localhost:3306/ml_account?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true",
            "root", "password", "classpath:migration/account");

        FlywayMigration.migrate(flywaySettings);
    }
}
