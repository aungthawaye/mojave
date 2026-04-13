package org.mojave.core.wallet.domain;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.identifier.wallet.NdcUpdateId;
import org.mojave.scheme.rule.identifier.wallet.WalletId;
import org.mojave.scheme.rule.identifier.wallet.WalletOwnerId;
import org.mojave.core.wallet.contract.command.CreateWalletCommand;
import org.mojave.core.wallet.domain.cache.WalletCache;
import org.mojave.core.wallet.domain.model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BaseIT {

    private static final String MYSQL_WALLET_DB_URL = "MYSQL_WALLET_DB_URL";

    private static final String MYSQL_WALLET_DB_USER = "MYSQL_WALLET_DB_USER";

    private static final String MYSQL_WALLET_DB_PASSWORD = "MYSQL_WALLET_DB_PASSWORD";

    private static final String MYSQL_WALLET_DB_CONNECTION_TIMEOUT = "MYSQL_WALLET_DB_CONNECTION_TIMEOUT";

    private static final String MYSQL_WALLET_DB_VALIDATION_TIMEOUT = "MYSQL_WALLET_DB_VALIDATION_TIMEOUT";

    private static final String MYSQL_WALLET_DB_MAX_LIFETIME_TIMEOUT = "MYSQL_WALLET_DB_MAX_LIFETIME_TIMEOUT";

    private static final String MYSQL_WALLET_DB_IDLE_TIMEOUT = "MYSQL_WALLET_DB_IDLE_TIMEOUT";

    private static final String MYSQL_WALLET_DB_KEEPALIVE_TIMEOUT = "MYSQL_WALLET_DB_KEEPALIVE_TIMEOUT";

    private static final String MYSQL_WALLET_DB_MIN_POOL_SIZE = "MYSQL_WALLET_DB_MIN_POOL_SIZE";

    private static final String MYSQL_WALLET_DB_MAX_POOL_SIZE = "MYSQL_WALLET_DB_MAX_POOL_SIZE";

    private static final String WRITE_DB_URL = "jdbc:mysql://localhost:3306/mv_mojave?createDatabaseIfNotExist=true";

    private static final String WRITE_DB_USER = "root";

    private static final String WRITE_DB_PASSWORD = "password";

    static {

        configureWalletDomainDependenciesEnvironment();
        resetWalletSchemaForTests();
    }

    @Autowired
    private WalletCache walletCache;

    @BeforeAll
    public static void beforeAll() {

        WalletFlyway.migrate(WRITE_DB_URL, WRITE_DB_USER, WRITE_DB_PASSWORD);
    }

    private static void configureWalletDomainDependenciesEnvironment() {

        setEnvironmentOverride(MYSQL_WALLET_DB_URL, WRITE_DB_URL);
        setEnvironmentOverride(MYSQL_WALLET_DB_USER, WRITE_DB_USER);
        setEnvironmentOverride(MYSQL_WALLET_DB_PASSWORD, WRITE_DB_PASSWORD);
        setEnvironmentOverride(MYSQL_WALLET_DB_CONNECTION_TIMEOUT, "30000");
        setEnvironmentOverride(MYSQL_WALLET_DB_VALIDATION_TIMEOUT, "5000");
        setEnvironmentOverride(MYSQL_WALLET_DB_MAX_LIFETIME_TIMEOUT, "1800000");
        setEnvironmentOverride(MYSQL_WALLET_DB_IDLE_TIMEOUT, "600000");
        setEnvironmentOverride(MYSQL_WALLET_DB_KEEPALIVE_TIMEOUT, "300000");
        setEnvironmentOverride(MYSQL_WALLET_DB_MIN_POOL_SIZE, "2");
        setEnvironmentOverride(MYSQL_WALLET_DB_MAX_POOL_SIZE, "2");
    }

    private static void setEnvironmentOverride(final String key, final String value) {

        System.setProperty(key, value);
    }

    private static void resetWalletSchemaForTests() {

        try (final var connection = DriverManager.getConnection(
            WRITE_DB_URL, WRITE_DB_USER,
            WRITE_DB_PASSWORD); final var statement = connection.createStatement()) {

            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DROP TABLE IF EXISTS mwe_balance_update");
            statement.execute("DROP TABLE IF EXISTS mwe_position_update");
            statement.execute("DROP TABLE IF EXISTS mwe_ndc_update");
            statement.execute("DROP TABLE IF EXISTS mwe_wallet");
            statement.execute("DROP TABLE IF EXISTS wlt_wallet");
            statement.execute("DROP TABLE IF EXISTS flyway_mysql_wallet_engine_history");
            statement.execute("DROP TABLE IF EXISTS flyway_wallet_history");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");

        } catch (final SQLException e) {
            throw new RuntimeException("Unable to reset wallet schema for tests.", e);
        }
    }

    private static void truncateDomainTables() {

        try (final var connection = DriverManager.getConnection(
            WRITE_DB_URL, WRITE_DB_USER,
            WRITE_DB_PASSWORD); final var statement = connection.createStatement()) {

            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("TRUNCATE TABLE mwe_balance_update");
            statement.execute("TRUNCATE TABLE mwe_position_update");
            statement.execute("TRUNCATE TABLE mwe_ndc_update");
            statement.execute("TRUNCATE TABLE mwe_wallet");
            statement.execute("TRUNCATE TABLE wlt_wallet");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");

        } catch (final SQLException e) {
            throw new RuntimeException("Unable to truncate wallet tables.", e);
        }
    }

    @BeforeEach
    public void beforeEach() {

        truncateDomainTables();
        this.walletCache.clear();
    }

    protected WalletId createDefaultWallet(final CreateWalletCommand createWalletCommand,
                                           final long walletOwnerId, final Currency currency,
                                           final String name) {

        return this.createWallet(
            createWalletCommand, walletOwnerId, currency, Wallet.DEFAULT_TAG, name);
    }

    protected WalletId createWallet(final CreateWalletCommand createWalletCommand,
                                    final long walletOwnerId, final Currency currency,
                                    final String tag, final String name) {

        final var output = createWalletCommand.execute(
            new CreateWalletCommand.Input(
                new WalletOwnerId(walletOwnerId), currency, tag,
                name));

        return output.walletId();
    }

    protected void executeSql(final String... sqlStatements) {

        try (final var connection = DriverManager.getConnection(
            WRITE_DB_URL, WRITE_DB_USER,
            WRITE_DB_PASSWORD); final var statement = connection.createStatement()) {

            for (final var sqlStatement : sqlStatements) {
                statement.execute(sqlStatement);
            }

        } catch (final SQLException e) {
            throw new RuntimeException("Unable to execute SQL statements.", e);
        }
    }

    protected void updateWalletEngineSnapshot(final WalletId walletId, final BigDecimal balance,
                                              final BigDecimal position, final BigDecimal reserved,
                                              final BigDecimal ndc) {

        try (final var connection = DriverManager.getConnection(
            WRITE_DB_URL, WRITE_DB_USER,
            WRITE_DB_PASSWORD); final var statement = connection.prepareStatement("""
            UPDATE mwe_wallet
            SET balance = ?, position = ?, reserved = ?, ndc = ?
            WHERE wallet_id = ?
            """)) {

            statement.setBigDecimal(1, balance);
            statement.setBigDecimal(2, position);
            statement.setBigDecimal(3, reserved);
            statement.setBigDecimal(4, ndc);
            statement.setLong(5, walletId.getId());
            statement.executeUpdate();

        } catch (final SQLException e) {
            throw new RuntimeException("Unable to update wallet snapshot.", e);
        }
    }

    protected NdcUpdateSnapshot loadNdcUpdateSnapshot(final NdcUpdateId ndcUpdateId) {

        try (final var connection = DriverManager.getConnection(
            WRITE_DB_URL, WRITE_DB_USER,
            WRITE_DB_PASSWORD); final var statement = connection.prepareStatement("""
            SELECT action, amount, old_ndc, new_ndc, description
            FROM mwe_ndc_update
            WHERE ndc_update_id = ?
            """)) {

            statement.setLong(1, ndcUpdateId.getId());

            try (final var resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return new NdcUpdateSnapshot(
                        resultSet.getString("action"),
                        resultSet.getBigDecimal("amount"),
                        resultSet.getBigDecimal("old_ndc"),
                        resultSet.getBigDecimal("new_ndc"),
                        resultSet.getString("description"));
                }
            }

            throw new IllegalStateException(
                "Unable to find NDC update snapshot for ndcUpdateId: " + ndcUpdateId);

        } catch (final SQLException e) {
            throw new RuntimeException("Unable to load NDC update snapshot.", e);
        }
    }

    protected record NdcUpdateSnapshot(String action,
                                       BigDecimal amount,
                                       BigDecimal oldNdc,
                                       BigDecimal newNdc,
                                       String description) { }

}
