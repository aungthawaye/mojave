/*-
 * ================================================================================
 * Mojaloop OSS
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

package io.mojaloop.core.account.domain.component.ledger.strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.mojaloop.core.account.domain.component.ledger.Ledger;
import io.mojaloop.core.common.datatype.enums.account.Side;
import io.mojaloop.core.common.datatype.enums.trasaction.TransactionType;
import io.mojaloop.core.common.datatype.identifier.account.AccountId;
import io.mojaloop.core.common.datatype.identifier.account.LedgerMovementId;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MySqlLedger implements Ledger {

    private static final Logger LOGGER = LoggerFactory.getLogger(MySqlLedger.class);

    private final JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper;

    public MySqlLedger(LedgerDbSettings settings, ObjectMapper objectMapper) {

        assert settings != null;
        assert objectMapper != null;

        var config = new HikariConfig();

        config.setPoolName(settings.pool().name());
        config.setJdbcUrl(settings.connection().url());
        config.setUsername(settings.connection().username());
        config.setPassword(settings.connection().password());
        config.setDriverClassName(com.mysql.cj.jdbc.Driver.class.getName());

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", true);
        config.addDataSourceProperty("useLocalSessionState", true);
        config.addDataSourceProperty("rereadBatchedStatements", true);
        config.addDataSourceProperty("cacheResultSetMetadata", true);
        config.addDataSourceProperty("cacheServerConfiguration", true);
        config.addDataSourceProperty("elideSetAutoCommits", true);
        config.addDataSourceProperty("maintainTimeStats", false);

        config.setMaximumPoolSize(settings.pool().maxPool());
        config.setAutoCommit(settings.connection().autoCommit());

        this.jdbcTemplate = new JdbcTemplate(new HikariDataSource(config));
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Movement> post(List<Request> requests,
                               TransactionId transactionId,
                               Instant transactionAt,
                               TransactionType transactionType)
        throws InsufficientBalanceException, OverdraftExceededException, RestoreFailedException {

        try {

            var posting = requests.stream().map(request -> new Posting(
                request.ledgerMovementId().getId(), request.accountId().getId(), request.side().name(),
                request.amount().toPlainString(), transactionId.getId(), transactionAt.getEpochSecond(),
                transactionType.name())).toList();

            var postingJson = this.objectMapper.writeValueAsString(posting);
            LOGGER.debug("Posting to ledger: {}", postingJson);

            return this.jdbcTemplate.execute((ConnectionCallback<List<Movement>>) con -> {

                var stm = con.prepareCall("{call sp_post_ledger_batch_with_movements(?)}");

                stm.setString(1, postingJson);

                var movements = new ArrayList<Movement>();
                var hasResults = stm.execute();

                while (hasResults) {

                    try (var rs = stm.getResultSet();) {

                        if (rs != null && rs.next()) {

                            var status = rs.getString("status");
                            LOGGER.debug("Ledger movement status: {}", status);

                            if ("ERROR".equals(status)) {

                                var code = rs.getString("code");
                                LOGGER.debug("Ledger movement error code: {}", code);

                                switch (code) {

                                    case "INSUFFICIENT_BALANCE": {

                                        var accountId = rs.getLong("account_id");
                                        var side = rs.getString("side");
                                        var amount = rs.getBigDecimal("amount");
                                        var debits = rs.getBigDecimal("debits");
                                        var credits = rs.getBigDecimal("credits");

                                        throw new RuntimeException(new InsufficientBalanceException(
                                            new AccountId(accountId), Side.valueOf(side), amount,
                                            new Ledger.DrCr(debits, credits)));
                                    }

                                    case "OVERDRAFT_EXCEEDED": {

                                        var accountId = rs.getLong("account_id");
                                        var side = rs.getString("side");
                                        var amount = rs.getBigDecimal("amount");
                                        var debits = rs.getBigDecimal("debits");
                                        var credits = rs.getBigDecimal("credits");

                                        throw new RuntimeException(new OverdraftExceededException(
                                            new AccountId(accountId), Side.valueOf(side), amount,
                                            new Ledger.DrCr(debits, credits)));
                                    }

                                    case "RESTORE_FAILED": {

                                        var accountId = rs.getLong("account_id");
                                        var side = rs.getString("side");
                                        var amount = rs.getBigDecimal("amount");
                                        var debits = rs.getBigDecimal("debits");
                                        var credits = rs.getBigDecimal("credits");

                                        throw new RuntimeException(new RestoreFailedException(
                                            new AccountId(accountId), Side.valueOf(side), amount,
                                            new Ledger.DrCr(debits, credits)));
                                    }

                                    default:
                                        throw new NoMovementResultException();
                                }
                            } else if ("SUCCESS".equals(status)) {

                                do {

                                    var ledgerMovementId = rs.getLong("ledger_movement_id");
                                    var accountId = rs.getLong("account_id");
                                    var side = rs.getString("side");
                                    var amount = rs.getBigDecimal("amount");
                                    var oldDebits = rs.getBigDecimal("old_debits");
                                    var oldCredits = rs.getBigDecimal("old_credits");
                                    var newDebits = rs.getBigDecimal("new_debits");
                                    var newCredits = rs.getBigDecimal("new_credits");

                                    var movement = new Movement(
                                        new LedgerMovementId(ledgerMovementId), new AccountId(accountId),
                                        Side.valueOf(side), amount, new DrCr(oldDebits, oldCredits),
                                        new DrCr(newDebits, newCredits), transactionId, transactionAt, transactionType);

                                    LOGGER.debug("Ledger movement: {}", movement);

                                    movements.add(movement);

                                } while (rs.next());

                            } else if ("IGNORED".equals(status)) {

                                return movements;

                            } else {

                                throw new NoMovementResultException();
                            }
                        }
                    }

                    hasResults = stm.getMoreResults();
                }

                return movements;

            });

        } catch (RuntimeException e) {

            if (e.getCause() instanceof NegativeAmountException e1) {
                throw e1;
            }

            if (e.getCause() instanceof SqlErrorOccurredException e1) {
                throw e1;
            }

            if (e.getCause() instanceof NoMovementResultException e1) {
                throw e1;
            }

            if (e.getCause() instanceof InsufficientBalanceException e1) {
                throw e1;
            }

            if (e.getCause() instanceof OverdraftExceededException e1) {
                throw e1;
            }

            if (e.getCause() instanceof RestoreFailedException e1) {
                throw e1;
            }

            throw e;

        } catch (JsonProcessingException e) {

            LOGGER.error("Unable to serialize ledger movement request", e);
            throw new RuntimeException(e);
        }
    }

    public record LedgerDbSettings(LedgerDbSettings.Connection connection, LedgerDbSettings.Pool pool) {

        public record Connection(String url, String username, String password, boolean autoCommit) { }

        public record Pool(String name, int minPool, int maxPool) { }

    }

    private record Posting(long ledgerMovementId,
                           long accountId,
                           String side,
                           String amount,
                           long transactionId,
                           long transactionAt,
                           String transactionType) { }

}
