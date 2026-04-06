package org.mojave.wallet.domain;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.wallet.BalanceAction;
import org.mojave.common.datatype.enums.wallet.PositionAction;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.common.datatype.identifier.wallet.BalanceUpdateId;
import org.mojave.common.datatype.identifier.wallet.PositionUpdateId;
import org.mojave.common.datatype.identifier.wallet.WalletId;
import org.mojave.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.wallet.contract.command.CreateWalletCommand;
import org.mojave.wallet.contract.engine.WalletEngine;
import org.mojave.wallet.domain.model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Instant;

public class BaseIT {

    private static final String WRITE_DB_URL = "jdbc:mysql://localhost:3306/mv_mojave?createDatabaseIfNotExist=true";

    private static final String WRITE_DB_USER = "root";

    private static final String WRITE_DB_PASSWORD = "password";

    @Autowired(required = false)
    protected WalletDomainSettings.TestWalletEngine testWalletEngine;

    @BeforeAll
    public static void beforeAll() {

        WalletFlyway.migrate(WRITE_DB_URL, WRITE_DB_USER, WRITE_DB_PASSWORD);
    }

    @BeforeEach
    public void beforeEach() {

        truncateDomainTables();

        if (this.testWalletEngine != null) {
            this.testWalletEngine.reset();
        }
    }

    protected WalletId createWallet(final CreateWalletCommand createWalletCommand,
                                    final long walletOwnerId,
                                    final Currency currency,
                                    final String scenario,
                                    final String name) {

        final var output = createWalletCommand.execute(
            new CreateWalletCommand.Input(
                new WalletOwnerId(walletOwnerId), currency, scenario, name));

        return output.walletId();
    }

    protected WalletId createDefaultWallet(final CreateWalletCommand createWalletCommand,
                                           final long walletOwnerId,
                                           final Currency currency,
                                           final String name) {

        return this.createWallet(
            createWalletCommand, walletOwnerId, currency, Wallet.DEFAULT_SCENARIO, name);
    }

    protected WalletEngine.BalanceHistory balanceHistory(final BalanceUpdateId balanceUpdateId,
                                                         final WalletId walletId,
                                                         final BalanceAction action,
                                                         final TransactionId transactionId,
                                                         final Currency currency,
                                                         final BigDecimal amount,
                                                         final BigDecimal oldBalance,
                                                         final BigDecimal newBalance,
                                                         final Instant transactionAt,
                                                         final BalanceUpdateId reversalId) {

        return new WalletEngine.BalanceHistory(
            balanceUpdateId, walletId, action, transactionId, currency, amount, oldBalance,
            newBalance, transactionAt, reversalId);
    }

    protected WalletEngine.FulfilResult fulfilResult(final PositionUpdateId payerCommitmentId,
                                                     final PositionUpdateId payeeCommitmentId) {

        return new WalletEngine.FulfilResult(payerCommitmentId, payeeCommitmentId);
    }

    protected WalletEngine.PositionHistory positionHistory(final PositionUpdateId positionUpdateId,
                                                           final WalletId walletId,
                                                           final PositionAction action,
                                                           final TransactionId transactionId,
                                                           final Currency currency,
                                                           final BigDecimal amount,
                                                           final BigDecimal oldPosition,
                                                           final BigDecimal newPosition,
                                                           final BigDecimal oldReserved,
                                                           final BigDecimal newReserved,
                                                           final BigDecimal netDebitCap,
                                                           final Instant transactionAt) {

        return new WalletEngine.PositionHistory(
            positionUpdateId, walletId, action, transactionId, currency, amount, oldPosition,
            newPosition, oldReserved, newReserved, netDebitCap, transactionAt);
    }

    private static void truncateDomainTables() {

        try (final var connection = DriverManager.getConnection(
            WRITE_DB_URL, WRITE_DB_USER,
            WRITE_DB_PASSWORD); final var statement = connection.createStatement()) {

            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("TRUNCATE TABLE wlt_wallet");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");

        } catch (final SQLException e) {
            throw new RuntimeException("Unable to truncate wallet tables.", e);
        }
    }

}
