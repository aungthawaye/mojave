/*-
 * ================================================================================
 * Mojave
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */
package org.mojave.core.wallet.domain.component.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.mojave.core.common.datatype.enums.wallet.PositionAction;
import org.mojave.core.common.datatype.identifier.transaction.TransactionId;
import org.mojave.core.common.datatype.identifier.wallet.PositionId;
import org.mojave.core.common.datatype.identifier.wallet.PositionUpdateId;
import org.mojave.core.wallet.domain.component.PositionUpdater;
import org.mojave.fspiop.spec.core.Currency;
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

        // Basic
        config.setPoolName(settings.pool().name());
        config.setJdbcUrl(settings.connection().url());
        config.setUsername(settings.connection().username());
        config.setPassword(settings.connection().password());
        config.setDriverClassName(com.mysql.cj.jdbc.Driver.class.getName());

        // ---- MySQL driver performance flags ----
        // Statement cache
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "500");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "4096");

        // Batch optimization (if you use batch)
        config.addDataSourceProperty("rewriteBatchedStatements", "true");

        // Metadata / session state caches
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");

        // Server-side prepared statements
        // If you *know* CALL metadata bugs are not an issue in your env, turn this ON for higher TPS:
        config.addDataSourceProperty("useServerPrepStmts", "true");
        // If you still hit CALL issues, set to false and keep callableStmtCacheSize = 0
        // config.addDataSourceProperty("useServerPrepStmts", "false");
        config.addDataSourceProperty("callableStmtCacheSize", "100");

        // Optional: keep your previous "useResetSession" behaviour
        config.addDataSourceProperty("useResetSession", "true");

        // ---- Hikari pool sizing ----
        config.setMaximumPoolSize(settings.pool().maxPool());
        // For no permanent idle connections: pool can shrink to 0 when app is idle
        config.setMinimumIdle(0);

        // ---- Timeouts (fail fast under high TPS) ----
        config.setConnectionTimeout(250);         // ms – tune (200–500ms typical)
        config.setValidationTimeout(1000);        // ms – how long isValid() may take
        config.setAutoCommit(true);               // fine for most OLTP workloads

        // ---- Idle behaviour & “no idle wakeup” to DB ----

        // 1) Do NOT set a connectionTestQuery
        //    Hikari will use connection.isValid() only when a connection is borrowed.

        // 2) Disable keepalive pings – no queries while idle.
        config.setKeepaliveTime(0L);              // default, but set explicitly for clarity

        // 3) Let the pool close connections when app is idle, so DB sees *zero* connections
        //    after some quiet period. No idle queries because there are no connections.
        //
        //    Example: after 60s of zero usage, shrink pool to 0.
        config.setIdleTimeout(60_000L);           // 60s – adjust as you like

        // 4) Reasonable max lifetime to avoid stale connections,
        //    but still no idle test queries.
        //    Make this a bit less than MySQL wait_timeout if you changed it.
        config.setMaxLifetime(30 * 60_000L);      // 30 minutes

        // If you want to *never* recycle connections proactively (not generally recommended):
        // config.setIdleTimeout(0L);             // don't reap idles
        // config.setMaxLifetime(0L);            // infinite lifetime; DB/firewall will close

        this.jdbcTemplate = new JdbcTemplate(new HikariDataSource(config));
    }

    private static PositionHistory mapHistory(java.sql.ResultSet rs) throws java.sql.SQLException {

        return new PositionHistory(
            new PositionUpdateId(rs.getLong("position_update_id")),
            new PositionId(rs.getLong("position_id")),
            PositionAction.valueOf(rs.getString("action")),
            new TransactionId(rs.getLong("transaction_id")),
            Currency.valueOf(rs.getString("currency")), rs.getBigDecimal("amount"),
            rs.getBigDecimal("old_position"), rs.getBigDecimal("new_position"),
            rs.getBigDecimal("old_reserved"), rs.getBigDecimal("new_reserved"),
            rs.getBigDecimal("ndc"), Instant.ofEpochSecond(rs.getLong("transaction_at")));
    }

    @Override
    public PositionHistory commit(final PositionUpdateId reservationId,
                                  final PositionUpdateId positionUpdateId)
        throws CommitFailedException {

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
                                    throw new RuntimeException(
                                        new CommitFailedException(reservationId));
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

        try {
            return this.jdbcTemplate.execute((ConnectionCallback<PositionHistory>) con -> {

                try (
                    var stm = con.prepareStatement("CALL sp_decrease_position(?, ?, ?, ?, ?, ?)")) {

                    stm.setLong(1, transactionId.getId());
                    stm.setLong(2, transactionAt.getEpochSecond());
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
    public FulfilResult fulfil(PositionUpdateId reservationId,
                               PositionUpdateId reservationCommitId,
                               PositionUpdateId positionDecrementId,
                               PositionId payeePositionId,
                               String description) throws NoPositionFulfilmentException {

        try {

            return this.jdbcTemplate.execute((ConnectionCallback<FulfilResult>) con -> {

                try (var stm = con.prepareStatement("CALL sp_fulfil_positions(?, ?, ?, ?, ?)")) {

                    stm.setLong(1, reservationId.getId());
                    stm.setLong(2, reservationCommitId.getId());
                    stm.setLong(3, positionDecrementId.getId());
                    stm.setLong(4, payeePositionId.getId());
                    stm.setString(5, description);

                    var hasResults = stm.execute();

                    while (hasResults) {
                        try (var rs = stm.getResultSet()) {
                            if (rs != null && rs.next()) {
                                var status = rs.getString("status");

                                if ("SUCCESS".equals(status)) {

                                    var payerCommitId = new PositionUpdateId(
                                        rs.getLong("payer_commit_id"));

                                    var payeeCommitId = new PositionUpdateId(
                                        rs.getLong("payee_commit_id"));

                                    return new FulfilResult(payerCommitId, payeeCommitId);

                                } else {

                                    throw new RuntimeException(
                                        new PositionUpdater.NoPositionFulfilmentException(
                                            reservationId));
                                }
                            }
                        }
                        hasResults = stm.getMoreResults();
                    }

                    throw new RuntimeException(
                        "No fulfil result returned for reservationId: " + reservationId);
                }
            });

        } catch (RuntimeException e) {

            if (e.getCause() instanceof NoPositionFulfilmentException e1) {
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
                                    final String description)
        throws NoPositionUpdateException, LimitExceededException {

        try {
            return this.jdbcTemplate.execute((ConnectionCallback<PositionHistory>) con -> {
                try (
                    var stm = con.prepareStatement("CALL sp_increase_position(?, ?, ?, ?, ?, ?)")) {

                    stm.setLong(1, transactionId.getId());
                    stm.setLong(2, transactionAt.getEpochSecond());
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

                                    throw new RuntimeException(new LimitExceededException(
                                        positionId, amount, rs.getBigDecimal("old_position"),
                                        rs.getBigDecimal("old_reserved"), rs.getBigDecimal("ndc"),
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
                                   final String description)
        throws NoPositionUpdateException, LimitExceededException {

        try {
            return this.jdbcTemplate.execute((ConnectionCallback<PositionHistory>) con -> {
                try (var stm = con.prepareStatement("CALL sp_reserve_position(?, ?, ?, ?, ?, ?)")) {

                    stm.setLong(1, transactionId.getId());
                    stm.setLong(2, transactionAt.getEpochSecond());
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

                                    throw new RuntimeException(new LimitExceededException(
                                        positionId, amount, rs.getBigDecimal("old_position"),
                                        rs.getBigDecimal("old_reserved"), rs.getBigDecimal("ndc"),
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
    public PositionHistory rollback(final PositionUpdateId reservationId,
                                    final PositionUpdateId positionUpdateId)
        throws RollbackFailedException {

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

                                    throw new RuntimeException(
                                        new RollbackFailedException(reservationId));
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
