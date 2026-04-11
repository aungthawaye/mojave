package org.mojave.core.wallet.engine.mysql;

import com.mysql.cj.jdbc.Driver;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.common.datatype.identifier.wallet.BalanceUpdateId;
import org.mojave.common.datatype.identifier.wallet.NdcUpdateId;
import org.mojave.common.datatype.identifier.wallet.PositionUpdateId;
import org.mojave.common.datatype.identifier.wallet.WalletId;
import org.mojave.component.flyway.FlywayMigration;
import org.mojave.core.wallet.contract.engine.WalletEngine;
import org.mojave.core.wallet.engine.mysql.task.CommitPositionReservationTask;
import org.mojave.core.wallet.engine.mysql.task.CreateWalletTask;
import org.mojave.core.wallet.engine.mysql.task.DecreaseNdcTask;
import org.mojave.core.wallet.engine.mysql.task.DecreasePositionTask;
import org.mojave.core.wallet.engine.mysql.task.DepositBalanceTask;
import org.mojave.core.wallet.engine.mysql.task.FulfilPositionsTask;
import org.mojave.core.wallet.engine.mysql.task.IncreaseNdcTask;
import org.mojave.core.wallet.engine.mysql.task.IncreasePositionTask;
import org.mojave.core.wallet.engine.mysql.task.RefundBalanceTask;
import org.mojave.core.wallet.engine.mysql.task.ReservePositionTask;
import org.mojave.core.wallet.engine.mysql.task.RollbackPositionReservationTask;
import org.mojave.core.wallet.engine.mysql.task.WithdrawBalanceTask;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class MySqlWalletEngine implements WalletEngine {

    private static final String FLYWAY_WALLET_ENGINE_HISTORY = "flyway_mysql_wallet_engine_history";

    private static final String[] FLYWAY_LOCATIONS = {"classpath:migration/wallet-engine/mysql"};

    private final JdbcTemplate jdbcTemplate;

    public MySqlWalletEngine(final WalletDbSettings settings) {

        Objects.requireNonNull(settings);

        final var flyway = FlywayMigration.configure(
            new FlywayMigration.Settings(
                settings.connection().url(), settings.connection().username(),
                settings.connection().password(), FLYWAY_WALLET_ENGINE_HISTORY, FLYWAY_LOCATIONS));

        flyway.migrate();

        this.jdbcTemplate = new JdbcTemplate(new HikariDataSource(buildConfig(
            settings.pool().name(), settings.connection().url(), settings.connection().username(),
            settings.connection().password(), settings.connection().connectionTimeout(),
            settings.connection().validationTimeout(), settings.connection().maxLifetime(),
            settings.connection().idleTimeout(), settings.connection().keepaliveTime(),
            settings.connection().autoCommit(), settings.pool().minPool(),
            settings.pool().maxPool())));
    }

    private static HikariConfig buildConfig(final String poolName,
                                            final String jdbcUrl,
                                            final String username,
                                            final String password,
                                            final long connectionTimeout,
                                            final long validationTimeout,
                                            final long maxLifetime,
                                            final long idleTimeout,
                                            final long keepaliveTime,
                                            final boolean autoCommit,
                                            final int minPool,
                                            final int maxPool) {

        final var config = new HikariConfig();

        config.setPoolName(poolName);
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(Driver.class.getName());

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "500");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "4096");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("callableStmtCacheSize", "100");
        config.addDataSourceProperty("useResetSession", "true");

        config.setMaximumPoolSize(maxPool);
        config.setMinimumIdle(minPool);

        config.setConnectionTimeout(connectionTimeout);
        config.setValidationTimeout(validationTimeout);
        config.setAutoCommit(autoCommit);

        config.setKeepaliveTime(keepaliveTime);
        config.setIdleTimeout(idleTimeout);
        config.setMaxLifetime(maxLifetime);

        return config;
    }

    @Override
    public PositionHistory commitPositionReservation(final PositionUpdateId nextPositionUpdateId,
                                                     final PositionUpdateId reservationId)
        throws PositionReservationCommitFailedException {

        return CommitPositionReservationTask.execute(
            this.jdbcTemplate, nextPositionUpdateId, reservationId);
    }

    @Override
    public void createWallet(final WalletId walletId,
                             final Currency currency,
                             final int scale,
                             final String tag)
        throws WalletIdAlreadyTakenException {

        CreateWalletTask.execute(this.jdbcTemplate, walletId, currency, scale, tag);
    }

    @Override
    public NdcHistory decreaseNdc(final NdcUpdateId ndcUpdateId,
                                  final TransactionId transactionId,
                                  final Instant transactionAt,
                                  final WalletId walletId,
                                  final BigDecimal amount,
                                  final String description)
        throws NoBalanceUpdateException, PositionReservedExceedsNdcException {

        return DecreaseNdcTask.execute(
            this.jdbcTemplate, ndcUpdateId, transactionId, transactionAt, walletId, amount,
            description);
    }

    @Override
    public PositionHistory decreasePosition(final PositionUpdateId nextPositionUpdateId,
                                            final TransactionId transactionId,
                                            final Instant transactionAt,
                                            final WalletId walletId,
                                            final BigDecimal amount,
                                            final String description)
        throws NoPositionUpdateException {

        return DecreasePositionTask.execute(
            this.jdbcTemplate, nextPositionUpdateId, transactionId, transactionAt, walletId,
            amount, description);
    }

    @Override
    public BalanceHistory depositBalance(final TransactionId transactionId,
                                         final Instant transactionAt,
                                         final WalletId walletId,
                                         final BigDecimal amount,
                                         final String description,
                                         final BalanceUpdateId nextBalanceUpdateId)
        throws WalletEngine.NoBalanceUpdateException {

        return DepositBalanceTask.execute(
            this.jdbcTemplate, transactionId, transactionAt, walletId, amount, description,
            nextBalanceUpdateId);
    }

    @Override
    public FulfilResult fulfil(final PositionUpdateId reservationId,
                               final PositionUpdateId reservationCommitId,
                               final PositionUpdateId positionDecrementId,
                               final WalletId payeeWalletId,
                               final String description) throws NoPositionFulfilmentException {

        return FulfilPositionsTask.execute(
            this.jdbcTemplate, reservationId, reservationCommitId, positionDecrementId,
            payeeWalletId, description);
    }

    @Override
    public NdcHistory increaseNdc(final NdcUpdateId ndcUpdateId,
                                  final TransactionId transactionId,
                                  final Instant transactionAt,
                                  final WalletId walletId,
                                  final BigDecimal amount,
                                  final String description)
        throws NoBalanceUpdateException, BalanceLowerThanNewNdcException {

        return IncreaseNdcTask.execute(
            this.jdbcTemplate, ndcUpdateId, transactionId, transactionAt, walletId, amount,
            description);
    }

    @Override
    public PositionHistory increasePosition(final PositionUpdateId nextPositionUpdateId,
                                            final TransactionId transactionId,
                                            final Instant transactionAt,
                                            final WalletId walletId,
                                            final BigDecimal amount,
                                            final String description)
        throws NoPositionUpdateException, PositionLimitExceededException {

        return IncreasePositionTask.execute(
            this.jdbcTemplate, nextPositionUpdateId, transactionId, transactionAt, walletId,
            amount, description);
    }

    @Override
    public BalanceHistory refundBalance(final BalanceUpdateId reversalId,
                                        final BalanceUpdateId nextBalanceUpdateId)
        throws BalanceReversalFailedException {

        return RefundBalanceTask.execute(this.jdbcTemplate, reversalId, nextBalanceUpdateId);
    }

    @Override
    public PositionHistory reservePosition(final PositionUpdateId nextPositionUpdateId,
                                           final TransactionId transactionId,
                                           final Instant transactionAt,
                                           final WalletId walletId,
                                           final BigDecimal amount,
                                           final String description)
        throws NoPositionUpdateException, PositionLimitExceededException {

        return ReservePositionTask.execute(
            this.jdbcTemplate, nextPositionUpdateId, transactionId, transactionAt, walletId,
            amount, description);
    }

    @Override
    public PositionHistory rollbackPositionReservation(final PositionUpdateId nextPositionUpdateId,
                                                       final PositionUpdateId reservationId)
        throws PositionReservationRollbackFailedException {

        return RollbackPositionReservationTask.execute(
            this.jdbcTemplate, nextPositionUpdateId, reservationId);
    }

    @Override
    public BalanceHistory withdrawBalance(final BalanceUpdateId nextBalanceUpdateId,
                                          final TransactionId transactionId,
                                          final Instant transactionAt,
                                          final WalletId walletId,
                                          final BigDecimal amount,
                                          final String description)
        throws NoBalanceUpdateException, InsufficientBalanceException {

        return WithdrawBalanceTask.execute(
            this.jdbcTemplate, nextBalanceUpdateId, transactionId, transactionAt, walletId,
            amount, description);
    }

    public record WalletDbSettings(WalletDbSettings.Connection connection,
                                   WalletDbSettings.Pool pool) {

        public record Connection(String url,
                                 String username,
                                 String password,
                                 long connectionTimeout,
                                 long validationTimeout,
                                 long maxLifetime,
                                 long idleTimeout,
                                 long keepaliveTime,
                                 boolean autoCommit) { }

        public record Pool(String name, int minPool, int maxPool) { }

    }

}
