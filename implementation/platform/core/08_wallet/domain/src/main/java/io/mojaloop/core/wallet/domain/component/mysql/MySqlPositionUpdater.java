package io.mojaloop.core.wallet.domain.component.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.mojaloop.core.common.datatype.enums.wallet.PositionAction;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.core.wallet.domain.component.PositionUpdater;
import io.mojaloop.fspiop.spec.core.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * MySQL implementation of PositionUpdater that calls stored procedures for position operations.
 */
public class MySqlPositionUpdater implements PositionUpdater {

    private static final Logger LOGGER = LoggerFactory.getLogger(MySqlPositionUpdater.class);

    private final JdbcTemplate jdbcTemplate;

    public MySqlPositionUpdater(PositionDbSettings settings) {

        assert settings != null;

        var config = new HikariConfig();

        config.setPoolName(settings.pool().name());
        config.setJdbcUrl(settings.connection().url());
        config.setUsername(settings.connection().username());
        config.setPassword(settings.connection().password());
        config.setDriverClassName(com.mysql.cj.jdbc.Driver.class.getName());

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("rereadBatchedStatements", true);
        config.addDataSourceProperty("cacheResultSetMetadata", true);
        config.addDataSourceProperty("cacheServerConfiguration", true);
        config.addDataSourceProperty("elideSetAutoCommits", true);
        config.addDataSourceProperty("maintainTimeStats", false);
        // The below 2 lines are very IMPORTANT
        // Make sessions pristine between borrows
        config.addDataSourceProperty("useResetSession", true);
        // Let the driver manage true session state rather than local emulation
        config.addDataSourceProperty("useLocalSessionState", false);
        // Turn OFF server-side prepared statements for CALL (avoids metadata bugs)
        config.addDataSourceProperty("useServerPrepStmts", false);
        config.addDataSourceProperty("cacheResultSetMetadata", false);
        config.addDataSourceProperty("callableStmtCacheSize", "0");

        config.setMaximumPoolSize(settings.pool().maxPool());
        config.setAutoCommit(true);

        this.jdbcTemplate = new JdbcTemplate(new HikariDataSource(config));
    }

    private static PositionHistory mapHistory(java.sql.ResultSet rs) throws java.sql.SQLException {

        return new PositionHistory(new PositionUpdateId(rs.getLong("position_update_id")),
                                   new PositionId(rs.getLong("position_id")),
                                   PositionAction.valueOf(rs.getString("action")),
                                   new TransactionId(rs.getLong("transaction_id")),
                                   Currency.valueOf(rs.getString("currency")),
                                   rs.getBigDecimal("amount"),
                                   rs.getBigDecimal("old_position"),
                                   rs.getBigDecimal("new_position"),
                                   rs.getBigDecimal("old_reserved"),
                                   rs.getBigDecimal("new_reserved"),
                                   rs.getBigDecimal("net_debit_cap"),
                                   Instant.ofEpochMilli(rs.getLong("transaction_at")));
    }

    @Override
    public PositionHistory commit(final PositionUpdateId reservationId, final PositionUpdateId positionUpdateId) throws CommitFailedException {

        LOGGER.info("Commit reservationId: {}, positionUpdateId: {}", reservationId, positionUpdateId);

        try {
            return this.jdbcTemplate.execute((ConnectionCallback<PositionHistory>) con -> {
                try (var stm = con.prepareStatement("CALL sp_commit_position(?, ?)")) {

                    stm.setLong(1, reservationId.getId());
                    stm.setLong(2, positionUpdateId.getId());

                    var hasResults = stm.execute();

                    while (hasResults) {

                        try (var rs = stm.getResultSet()) {

                            if (rs != null && rs.next()) {

                                var status = rs.getString("status");

                                if ("SUCCESS".equals(status)) {

                                    return mapHistory(rs);

                                } else if ("COMMIT_FAILED".equals(status)) {
                                    throw new RuntimeException(new CommitFailedException(reservationId));
                                }
                            }
                        }
                        hasResults = stm.getMoreResults();
                    }

                    throw new RuntimeException(new CommitFailedException(reservationId));
                }
            });
        } catch (RuntimeException e) {

            if (e.getCause() instanceof CommitFailedException e1) {
                throw e1;
            }

            throw e;
        }
    }

    @Override
    public PositionHistory decrease(final TransactionId transactionId,
                                    final Instant transactionAt,
                                    final PositionUpdateId positionUpdateId,
                                    final PositionId positionId,
                                    final BigDecimal amount,
                                    final String description) throws NoPositionUpdateException {

        LOGGER.info("Decrease positionId: {}, amount: {}, transactionId: {}", positionId, amount, transactionId);

        try {
            return this.jdbcTemplate.execute((ConnectionCallback<PositionHistory>) con -> {
                try (var stm = con.prepareStatement("CALL sp_decrease_position(?, ?, ?, ?, ?, ?)")) {

                    stm.setLong(1, transactionId.getId());
                    stm.setLong(2, transactionAt.toEpochMilli());
                    stm.setLong(3, positionUpdateId.getId());
                    stm.setLong(4, positionId.getId());
                    stm.setBigDecimal(5, amount);
                    stm.setString(6, description);

                    var hasResults = stm.execute();

                    while (hasResults) {

                        try (var rs = stm.getResultSet()) {

                            if (rs != null && rs.next()) {

                                var status = rs.getString("status");

                                if ("SUCCESS".equals(status)) {
                                    return mapHistory(rs);
                                }
                            }
                        }
                        hasResults = stm.getMoreResults();
                    }

                    throw new RuntimeException(new NoPositionUpdateException(transactionId));
                }
            });
        } catch (RuntimeException e) {
            if (e.getCause() instanceof NoPositionUpdateException e1) {
                throw e1;
            }
            throw e;
        }
    }

    @Override
    public PositionHistory increase(final TransactionId transactionId,
                                    final Instant transactionAt,
                                    final PositionUpdateId positionUpdateId,
                                    final PositionId positionId,
                                    final BigDecimal amount,
                                    final String description) throws NoPositionUpdateException, LimitExceededException {

        LOGGER.info("Increase positionId: {}, amount: {}, transactionId: {}", positionId, amount, transactionId);

        try {
            return this.jdbcTemplate.execute((ConnectionCallback<PositionHistory>) con -> {
                try (var stm = con.prepareStatement("CALL sp_increase_position(?, ?, ?, ?, ?, ?)")) {

                    stm.setLong(1, transactionId.getId());
                    stm.setLong(2, transactionAt.toEpochMilli());
                    stm.setLong(3, positionUpdateId.getId());
                    stm.setLong(4, positionId.getId());
                    stm.setBigDecimal(5, amount);
                    stm.setString(6, description);

                    var hasResults = stm.execute();

                    while (hasResults) {

                        try (var rs = stm.getResultSet()) {

                            if (rs != null && rs.next()) {

                                var status = rs.getString("status");

                                if ("SUCCESS".equals(status)) {

                                    return mapHistory(rs);

                                } else if ("LIMIT_EXCEEDED".equals(status)) {

                                    throw new RuntimeException(new LimitExceededException(positionId,
                                                                                          amount,
                                                                                          rs.getBigDecimal("old_position"),
                                                                                          rs.getBigDecimal("net_debit_cap"),
                                                                                          transactionId));
                                }
                            }
                        }
                        hasResults = stm.getMoreResults();
                    }

                    throw new RuntimeException(new NoPositionUpdateException(transactionId));
                }
            });
        } catch (RuntimeException e) {
            if (e.getCause() instanceof NoPositionUpdateException e1) {
                throw e1;
            }
            if (e.getCause() instanceof LimitExceededException e1) {
                throw e1;
            }
            throw e;
        }
    }

    @Override
    public PositionHistory reserve(final TransactionId transactionId,
                                   final Instant transactionAt,
                                   final PositionUpdateId positionUpdateId,
                                   final PositionId positionId,
                                   final BigDecimal amount,
                                   final String description) throws NoPositionUpdateException, LimitExceededException {

        LOGGER.info("Reserve positionId: {}, amount: {}, transactionId: {}", positionId, amount, transactionId);

        try {
            return this.jdbcTemplate.execute((ConnectionCallback<PositionHistory>) con -> {
                try (var stm = con.prepareStatement("CALL sp_reserve_position(?, ?, ?, ?, ?, ?)")) {

                    stm.setLong(1, transactionId.getId());
                    stm.setLong(2, transactionAt.toEpochMilli());
                    stm.setLong(3, positionUpdateId.getId());
                    stm.setLong(4, positionId.getId());
                    stm.setBigDecimal(5, amount);
                    stm.setString(6, description);

                    var hasResults = stm.execute();

                    while (hasResults) {
                        try (var rs = stm.getResultSet()) {
                            if (rs != null && rs.next()) {

                                var status = rs.getString("status");

                                if ("SUCCESS".equals(status)) {

                                    return mapHistory(rs);

                                } else if ("LIMIT_EXCEEDED".equals(status)) {

                                    throw new RuntimeException(new LimitExceededException(positionId,
                                                                                          amount,
                                                                                          rs.getBigDecimal("old_position"),
                                                                                          rs.getBigDecimal("net_debit_cap"),
                                                                                          transactionId));
                                }
                            }
                        }

                        hasResults = stm.getMoreResults();
                    }

                    throw new RuntimeException(new NoPositionUpdateException(transactionId));
                }
            });
        } catch (RuntimeException e) {
            if (e.getCause() instanceof NoPositionUpdateException e1) {
                throw e1;
            }
            if (e.getCause() instanceof LimitExceededException e1) {
                throw e1;
            }
            throw e;
        }
    }

    @Override
    public PositionHistory rollback(final PositionUpdateId reservationId, final PositionUpdateId positionUpdateId) throws RollbackFailedException {

        LOGGER.info("Rollback reservationId: {}, positionUpdateId: {}", reservationId, positionUpdateId);

        try {
            return this.jdbcTemplate.execute((ConnectionCallback<PositionHistory>) con -> {
                try (var stm = con.prepareStatement("CALL sp_rollback_position(?, ?)")) {

                    stm.setLong(1, reservationId.getId());
                    stm.setLong(2, positionUpdateId.getId());

                    var hasResults = stm.execute();

                    while (hasResults) {

                        try (var rs = stm.getResultSet()) {

                            if (rs != null && rs.next()) {

                                var status = rs.getString("status");

                                if ("SUCCESS".equals(status)) {

                                    return mapHistory(rs);

                                } else if ("ROLLBACK_FAILED".equals(status)) {

                                    throw new RuntimeException(new RollbackFailedException(reservationId));
                                }
                            }
                        }
                        hasResults = stm.getMoreResults();
                    }

                    throw new RuntimeException(new RollbackFailedException(reservationId));
                }
            });
        } catch (RuntimeException e) {

            if (e.getCause() instanceof RollbackFailedException e1) {
                throw e1;
            }

            throw e;
        }
    }

    public record PositionDbSettings(Connection connection, Pool pool) {

        public record Connection(String url, String username, String password) { }

        public record Pool(String name, int minPool, int maxPool) { }

    }

}
