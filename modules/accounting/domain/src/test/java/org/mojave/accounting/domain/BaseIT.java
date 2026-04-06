package org.mojave.accounting.domain;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mojave.accounting.contract.command.account.CreateAccountCommand;
import org.mojave.accounting.contract.command.chart.CreateCoaCommand;
import org.mojave.accounting.contract.command.chart.CreateCoaEntryCommand;
import org.mojave.accounting.contract.command.definition.CreateFlowDefinitionCommand;
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
import org.mojave.scheme.rule.accounting.scenario.AccountingScenario;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class BaseIT {

    private static final String WRITE_DB_URL = "jdbc:mysql://localhost:3306/mv_mojave?createDatabaseIfNotExist=true";

    private static final String WRITE_DB_USER = "root";

    private static final String WRITE_DB_PASSWORD = "password";

    @Autowired(required = false)
    protected AccountingDomainSettings.TestLedgerEngine testLedger;

    @BeforeAll
    public static void beforeAll() {

        AccountingFlyway.migrate(WRITE_DB_URL, WRITE_DB_USER, WRITE_DB_PASSWORD);
    }

    private static void truncateDomainTables() {

        try (final var connection = DriverManager.getConnection(
            WRITE_DB_URL, WRITE_DB_USER,
            WRITE_DB_PASSWORD); final var statement = connection.createStatement()) {

            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("TRUNCATE TABLE acc_ledger_movement");
            statement.execute("TRUNCATE TABLE acc_ledger_balance");
            statement.execute("TRUNCATE TABLE acc_flow_line");
            statement.execute("TRUNCATE TABLE acc_flow_definition");
            statement.execute("TRUNCATE TABLE acc_account");
            statement.execute("TRUNCATE TABLE acc_coa_entry");
            statement.execute("TRUNCATE TABLE acc_coa");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");

        } catch (final SQLException e) {
            throw new RuntimeException("Unable to truncate accounting tables.", e);
        }
    }

    @BeforeEach
    public void beforeEach() {

        truncateDomainTables();

        if (this.testLedger != null) {
            this.testLedger.reset();
        }
    }

    protected AccountId createAccount(final CreateAccountCommand createAccountCommand,
                                      final CoaEntryId coaEntryId,
                                      final long ownerId,
                                      final Currency currency,
                                      final String code,
                                      final String name) {

        final var output = createAccountCommand.execute(
            new CreateAccountCommand.Input(
                coaEntryId, new AccountOwnerId(ownerId), currency, new AccountCode(code), name,
                name + " description", OverdraftMode.FORBID, BigDecimal.ZERO));

        return output.accountId();
    }

    protected CoaId createCoa(final CreateCoaCommand createCoaCommand, final String name) {

        final var output = createCoaCommand.execute(new CreateCoaCommand.Input(name));

        return output.coaId();
    }

    protected CoaEntryId createCoaEntry(final CreateCoaEntryCommand createCoaEntryCommand,
                                        final CoaId coaId,
                                        final String category,
                                        final String code,
                                        final String name,
                                        final AccountType accountType) {

        final var output = createCoaEntryCommand.execute(
            new CreateCoaEntryCommand.Input(
                coaId, category, new CoaEntryCode(code), name,
                name + " description", accountType));

        return output.coaEntryId();
    }

    protected FlowDefinitionId createFlowDefinition(final CreateFlowDefinitionCommand createFlowDefinitionCommand,
                                                    final AccountingScenario scenario,
                                                    final Currency currency,
                                                    final String name,
                                                    final CoaEntryId coaEntryId,
                                                    final String participant,
                                                    final String amountName,
                                                    final Side side) {

        final var output = createFlowDefinitionCommand.execute(
            new CreateFlowDefinitionCommand.Input(
                scenario, currency, name, name + " description", List.of(
                new CreateFlowDefinitionCommand.Input.FlowLine(
                    1, participant, coaEntryId, amountName, side,
                    name + " flow line description"))));

        return output.flowDefinitionId();
    }

    protected void updateFlowDefinitionStatus(final FlowDefinitionId flowDefinitionId,
                                              final String activationStatus,
                                              final String terminationStatus) {

        try (final var connection = DriverManager.getConnection(
            WRITE_DB_URL, WRITE_DB_USER,
            WRITE_DB_PASSWORD); final var statement = connection.prepareStatement("""
            UPDATE acc_flow_definition
            SET activation_status = ?, termination_status = ?
            WHERE flow_definition_id = ?
            """)) {

            statement.setString(1, activationStatus);
            statement.setString(2, terminationStatus);
            statement.setLong(3, flowDefinitionId.getId());
            statement.executeUpdate();

        } catch (final SQLException e) {
            throw new RuntimeException("Unable to update flow definition status.", e);
        }
    }

}
