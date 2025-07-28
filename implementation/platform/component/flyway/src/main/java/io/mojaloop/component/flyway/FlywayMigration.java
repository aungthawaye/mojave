package io.mojaloop.component.flyway;


import org.flywaydb.core.Flyway;

public class FlywayMigration {

    public static void migrate(Settings settings) {

        Flyway
            .configure()
            .dataSource(settings.url(), settings.username(), settings.password())
            .locations(settings.locations())
            .validateMigrationNaming(true)
            .validateOnMigrate(true)
            .baselineOnMigrate(true)
            .load()
            .migrate();
    }

    public record Settings(String url, String username, String password, String... locations) { }

}
