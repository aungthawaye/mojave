/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
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
 * ===
 */
package org.mojave.core.wallet.domain.component.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.mojave.core.common.datatype.enums.wallet.BalanceAction;
import org.mojave.core.common.datatype.identifier.transaction.TransactionId;
import org.mojave.core.common.datatype.identifier.wallet.BalanceId;
import org.mojave.core.common.datatype.identifier.wallet.BalanceUpdateId;
import org.mojave.core.wallet.domain.component.BalanceUpdater;
import org.mojave.core.common.datatype.enums.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class MySqlBalanceUpdater implements BalanceUpdater {

    private static final Logger LOGGER = LoggerFactory.getLogger(MySqlBalanceUpdater.class);

    private final JdbcTemplate jdbcTemplate;

    public MySqlBalanceUpdater(BalanceDbSettings settings) {

        Objects.requireNonNull(settings);

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
        config.setMinimumIdle(settings.pool().minPool());

        // ---- Timeouts (fail fast under high TPS) ----
        config.setConnectionTimeout(settings.connection().connectionTimeout());
        config.setValidationTimeout(settings.connection().validationTimeout());
        config.setAutoCommit(settings.connection().autoCommit());

        // ---- Idle behaviour & “no idle wakeup” to DB ----

        // 1) Do NOT set a connectionTestQuery
        //    Hikari will use connection.isValid() only when a connection is borrowed.

        // 2) Disable keepalive pings – no queries while idle.
        config.setKeepaliveTime(settings.connection().keepaliveTime());

        // 3) Let the pool close connections when app is idle, so DB sees *zero* connections
        //    after some quiet period. No idle queries because there are no connections.
        //
        //    Example: after 60s of zero usage, shrink pool to 0.
        config.setIdleTimeout(settings.connection().idleTimeout());

        // 4) Reasonable max lifetime to avoid stale connections,
        //    but still no idle test queries.
        //    Make this a bit less than MySQL wait_timeout if you changed it.
        config.setMaxLifetime(settings.connection().maxLifetime());

        this.jdbcTemplate = new JdbcTemplate(new HikariDataSource(config));
    }

    @Override
    public BalanceHistory deposit(TransactionId transactionId,
                                  Instant transactionAt,
                                  BalanceUpdateId balanceUpdateId,
                                  BalanceId balanceId,
                                  BigDecimal amount,
                                  String description) throws NoBalanceUpdateException {

        LOGGER.info(
            "Deposit transactionId: {}, balanceId: {}, amount: {}, description: {}", transactionId,
            balanceId, amount, description);

        try {

            return this.jdbcTemplate.execute((ConnectionCallback<BalanceHistory>) con -> {

                try (var stm = con.prepareStatement("CALL sp_deposit_fund(?, ?, ?, ?, ?, ?)")) {

                    stm.setLong(1, transactionId.getId());
                    stm.setLong(2, transactionAt.getEpochSecond());
                    stm.setLong(3, balanceUpdateId.getId());
                    stm.setLong(4, balanceId.getId());
                    stm.setBigDecimal(5, amount);
                    stm.setString(6, description);

                    var hasResults = stm.execute();

                    while (hasResults) {

                        try (var rs = stm.getResultSet()) {

                            if (rs != null && rs.next()) {

                                var status = rs.getString("result");

                                if ("SUCCESS".equals(status)) {

                                    return new BalanceHistory(
                                        new BalanceUpdateId(rs.getLong("balance_update_id")),
                                        new BalanceId(rs.getLong("balance_id")),
                                        BalanceAction.valueOf(rs.getString("action")),
                                        new TransactionId(rs.getLong("transaction_id")),
                                        Currency.valueOf(rs.getString("currency")),
                                        rs.getBigDecimal("amount"), rs.getBigDecimal("old_balance"),
                                        rs.getBigDecimal("new_balance"),
                                        Instant.ofEpochSecond(rs.getLong("transaction_at")), null);
                                }
                            }

                        }

                        hasResults = stm.getMoreResults();
                    }

                    throw new RuntimeException(new NoBalanceUpdateException(transactionId));
                }

            });
        } catch (RuntimeException e) {

            LOGGER.error(
                "Exception occurred while trying to deposit for transactionId : {}, amount : {}.",
                transactionId.getId(), amount.stripTrailingZeros().toPlainString(), e);

            if (e.getCause() instanceof NoBalanceUpdateException e1) {
                throw e1;
            }

            throw e;
        }
    }

    @Override
    public BalanceHistory reverse(BalanceUpdateId reversalId, BalanceUpdateId balanceUpdateId)
        throws ReversalFailedException {

        LOGGER.info("Reverse withdrawId: {}, balanceUpdateId: {}", reversalId, balanceUpdateId);

        try {

            return this.jdbcTemplate.execute((ConnectionCallback<BalanceHistory>) con -> {

                try (var stm = con.prepareStatement("CALL sp_reverse_fund(?, ?)")) {

                    stm.setLong(1, reversalId.getId());
                    stm.setLong(2, balanceUpdateId.getId());

                    var hasResults = stm.execute();

                    while (hasResults) {

                        try (var rs = stm.getResultSet()) {

                            if (rs != null && rs.next()) {

                                var status = rs.getString("status");

                                if ("SUCCESS".equals(status)) {

                                    return new BalanceHistory(
                                        new BalanceUpdateId(rs.getLong("balance_update_id")),
                                        new BalanceId(rs.getLong("balance_id")),
                                        BalanceAction.valueOf(rs.getString("action")),
                                        new TransactionId(rs.getLong("transaction_id")),
                                        Currency.valueOf(rs.getString("currency")),
                                        rs.getBigDecimal("amount"), rs.getBigDecimal("old_balance"),
                                        rs.getBigDecimal("new_balance"),
                                        Instant.ofEpochSecond(rs.getLong("transaction_at")),
                                        new BalanceUpdateId(rs.getLong("withdraw_id")));
                                } else if ("REVERSAL_FAILED".equals(status)) {

                                    throw new RuntimeException(new ReversalFailedException(
                                        new BalanceUpdateId(rs.getLong("withdraw_id"))));
                                }
                            }

                        }

                        hasResults = stm.getMoreResults();
                    }

                    throw new RuntimeException(new ReversalFailedException(reversalId));
                }

            });
        } catch (RuntimeException e) {

            LOGGER.error(
                "Exception occurred while trying to reverse for withdrawId : {}.",
                reversalId.getId(), e);

            if (e.getCause() instanceof ReversalFailedException e1) {
                throw e1;
            }

            throw e;
        }
    }

    @Override
    public BalanceHistory withdraw(TransactionId transactionId,
                                   Instant transactionAt,
                                   BalanceUpdateId balanceUpdateId,
                                   BalanceId balanceId,
                                   BigDecimal amount,
                                   String description)
        throws NoBalanceUpdateException, InsufficientBalanceException {

        LOGGER.info(
            "Withdraw transactionId: {}, balanceId: {}, amount: {}, description: {}", transactionId,
            balanceId, amount, description);

        try {

            return this.jdbcTemplate.execute((ConnectionCallback<BalanceHistory>) con -> {

                try (var stm = con.prepareStatement("CALL sp_withdraw_fund(?, ?, ?, ?, ?, ?)")) {

                    stm.setLong(1, transactionId.getId());
                    stm.setLong(2, transactionAt.toEpochMilli());
                    stm.setLong(3, balanceUpdateId.getId());
                    stm.setLong(4, balanceId.getId());
                    stm.setBigDecimal(5, amount);
                    stm.setString(6, description);

                    var hasResults = stm.execute();

                    while (hasResults) {

                        try (var rs = stm.getResultSet()) {

                            if (rs != null && rs.next()) {

                                var status = rs.getString("status");

                                if ("SUCCESS".equals(status)) {

                                    return new BalanceHistory(
                                        new BalanceUpdateId(rs.getLong("balance_update_id")),
                                        new BalanceId(rs.getLong("balance_id")),
                                        BalanceAction.valueOf(rs.getString("action")),
                                        new TransactionId(rs.getLong("transaction_id")),
                                        Currency.valueOf(rs.getString("currency")),
                                        rs.getBigDecimal("amount"), rs.getBigDecimal("old_balance"),
                                        rs.getBigDecimal("new_balance"),
                                        Instant.ofEpochSecond(rs.getLong("transaction_at")), null);
                                } else if ("INSUFFICIENT_BALANCE".equals(status)) {

                                    var balance = rs.getBigDecimal("old_balance");

                                    throw new RuntimeException(
                                        new InsufficientBalanceException(
                                            transactionId, balanceId,
                                            amount, balance));
                                }
                            }

                        }

                        hasResults = stm.getMoreResults();
                    }

                    throw new RuntimeException(new NoBalanceUpdateException(transactionId));
                }

            });
        } catch (RuntimeException e) {

            LOGGER.error(
                "Exception occurred while trying to withdraw for transactionId : {}, amount : {}.",
                transactionId.getId(), amount.stripTrailingZeros().toPlainString(), e);

            if (e.getCause() instanceof NoBalanceUpdateException e1) {
                throw e1;
            }

            if (e.getCause() instanceof InsufficientBalanceException e1) {
                throw e1;
            }

            throw e;
        }
    }

    public record BalanceDbSettings(BalanceDbSettings.Connection connection,
                                    BalanceDbSettings.Pool pool) {

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
