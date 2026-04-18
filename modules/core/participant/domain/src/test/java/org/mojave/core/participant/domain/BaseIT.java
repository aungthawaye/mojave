package org.mojave.core.participant.domain;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.enums.participant.EndpointType;
import org.mojave.scheme.rule.type.participant.FspCode;
import org.mojave.core.participant.contract.command.fsp.CreateFspCommand;
import org.mojave.core.participant.contract.command.hub.CreateHubCommand;

import java.sql.DriverManager;
import java.sql.SQLException;

public class BaseIT {

    private static final String WRITE_DB_URL = "jdbc:mysql://localhost:3306/mv_mojave?createDatabaseIfNotExist=true";

    private static final String WRITE_DB_USER = "root";

    private static final String WRITE_DB_PASSWORD = "password";

    @BeforeAll
    public static void beforeAll() {

        ParticipantFlyway.migrate(WRITE_DB_URL, WRITE_DB_USER, WRITE_DB_PASSWORD);
    }

    @BeforeEach
    public void beforeEach() {

        truncateDomainTables();
    }

    private static void truncateDomainTables() {

        try (final var connection = DriverManager.getConnection(
            WRITE_DB_URL, WRITE_DB_USER,
            WRITE_DB_PASSWORD);
             final var statement = connection.createStatement()) {

            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("TRUNCATE TABLE pcp_fsp_endpoint");
            statement.execute("TRUNCATE TABLE pcp_fsp_currency");
            statement.execute("TRUNCATE TABLE pcp_ssp_currency");
            statement.execute("TRUNCATE TABLE pcp_fsp");
            statement.execute("TRUNCATE TABLE pcp_ssp");
            statement.execute("TRUNCATE TABLE pcp_oracle");
            statement.execute("TRUNCATE TABLE pcp_hub_currency");
            statement.execute("TRUNCATE TABLE pcp_fsp_group");
            statement.execute("TRUNCATE TABLE pcp_hub");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");

        } catch (final SQLException e) {
            throw new RuntimeException("Unable to truncate participant tables.", e);
        }
    }

    protected void executeSql(final String... sqlStatements) {

        try (final var connection = DriverManager.getConnection(
            WRITE_DB_URL, WRITE_DB_USER,
            WRITE_DB_PASSWORD);
             final var statement = connection.createStatement()) {

            for (final var sqlStatement : sqlStatements) {
                statement.execute(sqlStatement);
            }

        } catch (final SQLException e) {
            throw new RuntimeException("Unable to execute SQL statements.", e);
        }
    }

    protected CreateHubCommand.Output createHub(final CreateHubCommand createHubCommand,
                                                final String name,
                                                final Currency... currencies) {

        return createHubCommand.execute(new CreateHubCommand.Input(name, currencies));
    }

    protected CreateFspCommand.Output createFsp(final CreateFspCommand createFspCommand,
                                                final String code,
                                                final String name,
                                                final Currency[] currencies,
                                                final CreateFspCommand.Input.Endpoint[] endpoints) {

        return createFspCommand.execute(
            new CreateFspCommand.Input(new FspCode(code), name, currencies, endpoints));
    }

    protected CreateFspCommand.Input.Endpoint endpoint(final EndpointType type,
                                                       final String baseUrl) {

        return new CreateFspCommand.Input.Endpoint(type, baseUrl);
    }

}
