package org.mojave.core.accounting.domain;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mojave.core.accounting.domain.cache.AccountCache;
import org.mojave.core.accounting.domain.cache.CoaEntryCache;
import org.mojave.core.accounting.domain.cache.FlowDefinitionCache;
import org.mojave.core.accounting.contract.command.account.CreateAccountCommand;
import org.mojave.core.accounting.contract.command.chart.CreateCoaCommand;
import org.mojave.core.accounting.contract.command.chart.CreateCoaEntryCommand;
import org.mojave.core.accounting.contract.command.definition.CreateFlowDefinitionCommand;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.accounting.AccountType;
import org.mojave.common.datatype.enums.accounting.OverdraftMode;
import org.mojave.common.datatype.enums.accounting.Side;
import org.mojave.common.datatype.identifier.accounting.AccountId;
import org.mojave.common.datatype.identifier.accounting.AccountOwnerId;
import org.mojave.common.datatype.identifier.accounting.CoaEntryId;
import org.mojave.common.datatype.identifier.accounting.CoaId;
import org.mojave.common.datatype.identifier.accounting.FlowDefinitionId;
import org.mojave.common.datatype.type.accounting.AccountCode;
import org.mojave.common.datatype.type.accounting.CoaEntryCode;
import org.mojave.scheme.rule.scenario.ScenarioType;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class BaseIT {

    private static final String MYSQL_LEDGER_DB_URL = "MYSQL_LEDGER_DB_URL";

    private static final String MYSQL_LEDGER_DB_USER = "MYSQL_LEDGER_DB_USER";

    private static final String MYSQL_LEDGER_DB_PASSWORD = "MYSQL_LEDGER_DB_PASSWORD";

    private static final String MYSQL_LEDGER_DB_CONNECTION_TIMEOUT = "MYSQL_LEDGER_DB_CONNECTION_TIMEOUT";

    private static final String MYSQL_LEDGER_DB_VALIDATION_TIMEOUT = "MYSQL_LEDGER_DB_VALIDATION_TIMEOUT";

    private static final String MYSQL_LEDGER_DB_MAX_LIFETIME_TIMEOUT = "MYSQL_LEDGER_DB_MAX_LIFETIME_TIMEOUT";

    private static final String MYSQL_LEDGER_DB_IDLE_TIMEOUT = "MYSQL_LEDGER_DB_IDLE_TIMEOUT";

    private static final String MYSQL_LEDGER_DB_KEEPALIVE_TIMEOUT = "MYSQL_LEDGER_DB_KEEPALIVE_TIMEOUT";

    private static final String MYSQL_LEDGER_DB_MIN_POOL_SIZE = "MYSQL_LEDGER_DB_MIN_POOL_SIZE";

    private static final String MYSQL_LEDGER_DB_MAX_POOL_SIZE = "MYSQL_LEDGER_DB_MAX_POOL_SIZE";

    private static final String WRITE_DB_URL = "jdbc:mysql://localhost:3306/mv_mojave?createDatabaseIfNotExist=true";

    private static final String WRITE_DB_USER = "root";

    private static final String WRITE_DB_PASSWORD = "password";

    @Autowired
    protected AccountCache accountCache;

    @Autowired
    protected CoaEntryCache coaEntryCache;

    @Autowired
    protected FlowDefinitionCache flowDefinitionCache;

    static {

        configureAccountingDomainDependenciesEnvironment();
    }

    @BeforeAll
    public static void beforeAll() {

        AccountingFlyway.migrate(WRITE_DB_URL, WRITE_DB_USER, WRITE_DB_PASSWORD);
    }

    private static void configureAccountingDomainDependenciesEnvironment() {

        setEnvironmentOverride(MYSQL_LEDGER_DB_URL, WRITE_DB_URL);
        setEnvironmentOverride(MYSQL_LEDGER_DB_USER, WRITE_DB_USER);
        setEnvironmentOverride(MYSQL_LEDGER_DB_PASSWORD, WRITE_DB_PASSWORD);
        setEnvironmentOverride(MYSQL_LEDGER_DB_CONNECTION_TIMEOUT, "30000");
        setEnvironmentOverride(MYSQL_LEDGER_DB_VALIDATION_TIMEOUT, "5000");
        setEnvironmentOverride(MYSQL_LEDGER_DB_MAX_LIFETIME_TIMEOUT, "1800000");
        setEnvironmentOverride(MYSQL_LEDGER_DB_IDLE_TIMEOUT, "600000");
        setEnvironmentOverride(MYSQL_LEDGER_DB_KEEPALIVE_TIMEOUT, "300000");
        setEnvironmentOverride(MYSQL_LEDGER_DB_MIN_POOL_SIZE, "2");
        setEnvironmentOverride(MYSQL_LEDGER_DB_MAX_POOL_SIZE, "2");
    }

    private static void setEnvironmentOverride(final String key, final String value) {

        System.setProperty(key, value);
    }

    private static void truncateDomainTables() {

        try (final var connection = DriverManager.getConnection(
            WRITE_DB_URL, WRITE_DB_USER,
            WRITE_DB_PASSWORD);
             final var statement = connection.createStatement()) {

            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("TRUNCATE TABLE acc_flow_definition_line");
            statement.execute("TRUNCATE TABLE acc_flow_definition");
            statement.execute("TRUNCATE TABLE acc_account");
            statement.execute("TRUNCATE TABLE acc_coa_entry");
            statement.execute("TRUNCATE TABLE acc_coa");
            statement.execute("TRUNCATE TABLE lgr_ledger_balance");
            statement.execute("TRUNCATE TABLE lgr_ledger_movement");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");

        } catch (final SQLException e) {
            throw new RuntimeException("Unable to truncate accounting tables.", e);
        }
    }

    @BeforeEach
    public void beforeEach() {

        truncateDomainTables();
        this.clearCaches();

    }

    private void clearCaches() {

        this.accountCache.clear();
        this.coaEntryCache.clear();
        this.flowDefinitionCache.clear();
    }

    protected AccountId createAccount(final CreateAccountCommand createAccountCommand,
                                      final CoaEntryId coaEntryId, final long ownerId,
                                      final Currency currency, final String code,
                                      final String name) {

        return this.createAccount(
            createAccountCommand, coaEntryId, ownerId, currency, code, name,
            OverdraftMode.FORBID, BigDecimal.ZERO);
    }

    protected AccountId createAccount(final CreateAccountCommand createAccountCommand,
                                      final CoaEntryId coaEntryId, final long ownerId,
                                      final Currency currency, final String code, final String name,
                                      final OverdraftMode overdraftMode,
                                      final BigDecimal overdraftLimit) {

        final var output = createAccountCommand.execute(
            new CreateAccountCommand.Input(
                coaEntryId, new AccountOwnerId(ownerId), currency, new AccountCode(code), name,
                name + " description", overdraftMode, overdraftLimit));

        return output.accountId();
    }

    protected CoaId createCoa(final CreateCoaCommand createCoaCommand, final String name) {

        final var output = createCoaCommand.execute(new CreateCoaCommand.Input(name));

        return output.coaId();
    }

    protected CoaEntryId createCoaEntry(final CreateCoaEntryCommand createCoaEntryCommand,
                                        final CoaId coaId, final String category, final String code,
                                        final String name, final AccountType accountType) {

        final var output = createCoaEntryCommand.execute(
            new CreateCoaEntryCommand.Input(
                coaId, category, new CoaEntryCode(code), name,
                name + " description", accountType));

        return output.coaEntryId();
    }

    protected FlowDefinitionId createFlowDefinition(
        final CreateFlowDefinitionCommand createFlowDefinitionCommand,
        final ScenarioType scenario, final Currency currency, final String name,
        final CoaEntryId coaEntryId, final String participant, final String amountName,
        final Side side) {

        final var output = createFlowDefinitionCommand.execute(
            new CreateFlowDefinitionCommand.Input(
                scenario, currency, name, name + " description", List.of(
                new CreateFlowDefinitionCommand.Input.FlowDefinitionLine(
                    1, participant, coaEntryId, amountName, side,
                    name + " flow definition line description"))));

        return output.flowDefinitionId();
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

    protected long queryForLong(final String sql) {

        try (final var connection = DriverManager.getConnection(
            WRITE_DB_URL, WRITE_DB_USER,
            WRITE_DB_PASSWORD);
             final var statement = connection.createStatement();
             final var resultSet = statement.executeQuery(sql)) {

            if (!resultSet.next()) {
                throw new IllegalStateException("Query did not return any row.");
            }

            return resultSet.getLong(1);

        } catch (final SQLException e) {
            throw new RuntimeException("Unable to execute SQL query.", e);
        }
    }

    protected void updateFlowDefinitionStatus(final FlowDefinitionId flowDefinitionId,
                                              final String activationStatus,
                                              final String terminationStatus) {

        try (final var connection = DriverManager.getConnection(
            WRITE_DB_URL, WRITE_DB_USER,
            WRITE_DB_PASSWORD);
             final var statement = connection.prepareStatement("""
                                                                   UPDATE acc_flow_definition
                                                                   SET activation_status = ?, termination_status = ?
                                                                   WHERE flow_definition_id = ?
                                                                   """)) {

            statement.setString(1, activationStatus);
            statement.setString(2, terminationStatus);
            statement.setLong(3, flowDefinitionId.getId());
            statement.executeUpdate();

            this.flowDefinitionCache.clear();

        } catch (final SQLException e) {
            throw new RuntimeException("Unable to update flow definition status.", e);
        }
    }

}
