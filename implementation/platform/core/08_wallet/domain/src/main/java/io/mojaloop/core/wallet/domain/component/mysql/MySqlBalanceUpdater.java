package io.mojaloop.core.wallet.domain.component.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.mojaloop.core.common.datatype.enums.wallet.BalanceAction;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.wallet.BalanceUpdateId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletId;
import io.mojaloop.core.wallet.domain.component.BalanceUpdater;
import io.mojaloop.fspiop.spec.core.Currency;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.Instant;

public class MySqlBalanceUpdater implements BalanceUpdater {

    private static final Logger LOGGER = LoggerFactory.getLogger(MySqlBalanceUpdater.class);

    private final JdbcTemplate jdbcTemplate;

    public MySqlBalanceUpdater(BalanceDbSettings settings) {

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
        // Ledger's stored-procedure will handle transaction.
        config.setAutoCommit(true);

        this.jdbcTemplate = new JdbcTemplate(new HikariDataSource(config));
    }

    @Override
    public BalanceHistory deposit(TransactionId transactionId,
                                  Instant transactionAt,
                                  BalanceUpdateId balanceUpdateId,
                                  WalletId walletId,
                                  BigDecimal amount,
                                  String description) throws NoBalanceUpdateException {

        LOGGER.info("Deposit transactionId: {}, walletId: {}, amount: {}, description: {}", transactionId, walletId,
                    amount, description);

        try {

            return this.jdbcTemplate.execute((ConnectionCallback<BalanceHistory>) con -> {

                try (var stm = con.prepareStatement("CALL sp_deposit_fund(?, ?, ?, ?, ?, ?)")) {

                    stm.setLong(1, transactionId.getId());
                    stm.setLong(2, transactionAt.getEpochSecond());
                    stm.setLong(3, balanceUpdateId.getId());
                    stm.setLong(4, walletId.getId());
                    stm.setBigDecimal(5, amount);
                    stm.setString(6, description);

                    var hasResults = stm.execute();

                    while (hasResults) {

                        try (var rs = stm.getResultSet()) {

                            if (rs != null && rs.next()) {

                                var status = rs.getString("status");

                                if ("SUCCESS".equals(status)) {

                                    return new BalanceHistory(new BalanceUpdateId(rs.getLong("balance_update_id")),
                                                              new WalletId(rs.getLong("wallet_id")),
                                                              BalanceAction.valueOf(rs.getString("action")),
                                                              new TransactionId(rs.getLong("transaction_id")),
                                                              Currency.valueOf(rs.getString("currency")),
                                                              rs.getBigDecimal("amount"),
                                                              rs.getBigDecimal("old_balance"),
                                                              rs.getBigDecimal("new_balance"),
                                                              Instant.ofEpochSecond(rs.getLong("transaction_at")));
                                }
                            }

                        }

                        hasResults = stm.getMoreResults();
                    }

                    throw new NoBalanceUpdateException(transactionId);

                } catch (NoBalanceUpdateException e) {

                    LOGGER.warn("No balance update found for transactionId: {}", transactionId);
                    throw new RuntimeException(e);
                }

            });
        } catch (RuntimeException e) {

            LOGGER.error("Exception occurred while trying to deposit for transactionId : {}, amount : {}.",
                         transactionId.getId(), amount.toPlainString(), e);

            if (e.getCause() instanceof NoBalanceUpdateException e1) {
                throw e1;
            }

            throw e;
        }
    }

    public record BalanceDbSettings(BalanceDbSettings.Connection connection, BalanceDbSettings.Pool pool) {

        public record Connection(String url, String username, String password) { }

        public record Pool(String name, int minPool, int maxPool) { }

    }

}
