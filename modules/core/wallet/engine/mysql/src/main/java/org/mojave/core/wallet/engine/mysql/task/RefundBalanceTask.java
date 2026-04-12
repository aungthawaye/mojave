package org.mojave.core.wallet.engine.mysql.task;

import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.enums.wallet.BalanceAction;
import org.mojave.scheme.rule.identifier.transaction.TransactionId;
import org.mojave.scheme.rule.identifier.wallet.BalanceUpdateId;
import org.mojave.scheme.rule.identifier.wallet.WalletId;
import org.mojave.core.wallet.contract.engine.WalletEngine;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

public final class RefundBalanceTask {

    private RefundBalanceTask() {

    }

    public static WalletEngine.BalanceHistory execute(final JdbcTemplate jdbcTemplate,
                                                      final BalanceUpdateId reversalId,
                                                      final BalanceUpdateId nextBalanceUpdateId)
        throws WalletEngine.BalanceReversalFailedException {

        try {

            return jdbcTemplate.execute((ConnectionCallback<WalletEngine.BalanceHistory>) con -> {

                try (var stm = con.prepareStatement("CALL sp_reverse_fund(?, ?)")) {

                    stm.setLong(1, reversalId.getId());
                    stm.setLong(2, nextBalanceUpdateId.getId());

                    var hasResults = stm.execute();

                    while (hasResults) {

                        try (var rs = stm.getResultSet()) {

                            if (rs != null && rs.next()) {

                                final var status = rs.getString("status");

                                if ("SUCCESS".equals(status)) {

                                    return resolveBalanceHistory(con, rs);
                                }

                                if ("REVERSAL_FAILED".equals(status)) {

                                    throw new RuntimeException(
                                        new WalletEngine.BalanceReversalFailedException(
                                            new BalanceUpdateId(rs.getLong("withdraw_id"))));
                                }
                            }

                        }

                        hasResults = stm.getMoreResults();
                    }

                    throw new RuntimeException(new WalletEngine.BalanceReversalFailedException(reversalId));
                }

            });
        } catch (final RuntimeException e) {

            if (e.getCause() instanceof WalletEngine.BalanceReversalFailedException e1) {
                throw e1;
            }

            throw e;
        }
    }

    private static WalletEngine.BalanceHistory resolveBalanceHistory(final Connection con,
                                                                     final ResultSet rs)
        throws SQLException {

        final var balanceUpdateId = rs.getLong("balance_update_id");
        final var withdrawId = new BalanceUpdateId(rs.getLong("withdraw_id"));

        try (var statement = con.prepareStatement("""
            SELECT balance_id,
                   action,
                   transaction_id,
                   currency,
                   amount,
                   old_balance,
                   new_balance,
                   transaction_at,
                   withdraw_id
            FROM mwe_balance_update
            WHERE balance_update_id = ?
            """)) {

            statement.setLong(1, balanceUpdateId);

            try (var result = statement.executeQuery()) {
                if (result.next()) {
                    final var currency = resolveCurrency(con, result);

                    return new WalletEngine.BalanceHistory(
                        new BalanceUpdateId(balanceUpdateId),
                        new WalletId(result.getLong("balance_id")),
                        BalanceAction.valueOf(result.getString("action")),
                        new TransactionId(result.getLong("transaction_id")),
                        Currency.valueOf(currency),
                        result.getBigDecimal("amount"),
                        result.getBigDecimal("old_balance"),
                        result.getBigDecimal("new_balance"),
                        Instant.ofEpochSecond(result.getLong("transaction_at")),
                        new BalanceUpdateId(result.getLong("withdraw_id")));
                }
            }
        }

        throw new IllegalStateException(
            "Balance update result not found for balance_update_id: " + balanceUpdateId +
                ", withdraw_id: " + withdrawId.getId());
    }

    private static String resolveCurrency(final Connection con, final ResultSet rs)
        throws SQLException {

        var currency = rs.getString("currency");

        if (currency != null) {
            return currency;
        }

        // Some MySQL procedure executions may return NULL in the immediate result row.
        // Resolve from the persisted record by id to keep the result deterministic.
        try (var statement = con.prepareStatement("""
            SELECT currency
            FROM mwe_balance_update
            WHERE balance_update_id = ?
            """)) {

            statement.setLong(1, rs.getLong("balance_update_id"));

            try (var result = statement.executeQuery()) {
                if (result.next()) {
                    currency = result.getString("currency");
                }
            }
        }

        if (currency == null) {
            throw new IllegalStateException(
                "Currency is null in balance history result for balance_update_id: " +
                    rs.getLong("balance_update_id"));
        }

        return currency;
    }

}
