package org.mojave.wallet.engine.mysql.task;

import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.common.datatype.identifier.wallet.BalanceUpdateId;
import org.mojave.common.datatype.identifier.wallet.WalletId;
import org.mojave.wallet.contract.engine.WalletEngine;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.Instant;

public final class WithdrawBalanceTask {

    private WithdrawBalanceTask() {

    }

    public static WalletEngine.BalanceHistory execute(final JdbcTemplate jdbcTemplate,
                                                      final BalanceUpdateId nextBalanceUpdateId,
                                                      final TransactionId transactionId,
                                                      final Instant transactionAt,
                                                      final WalletId walletId,
                                                      final BigDecimal amount,
                                                      final String description)
        throws WalletEngine.NoBalanceUpdateException, WalletEngine.InsufficientBalanceException {

        try {

            return jdbcTemplate.execute((ConnectionCallback<WalletEngine.BalanceHistory>) con -> {

                try (var stm = con.prepareStatement("CALL sp_withdraw_fund(?, ?, ?, ?, ?, ?)")) {

                    stm.setLong(1, transactionId.getId());
                    stm.setLong(2, transactionAt.getEpochSecond());
                    stm.setLong(3, nextBalanceUpdateId.getId());
                    stm.setLong(4, walletId.getId());
                    stm.setBigDecimal(5, amount);
                    stm.setString(6, description);

                    var hasResults = stm.execute();

                    while (hasResults) {

                        try (var rs = stm.getResultSet()) {

                            if (rs != null && rs.next()) {

                                final var status = rs.getString("status");

                                if ("SUCCESS".equals(status)) {

                                    return WalletTaskMapper.mapBalanceHistory(rs, null);
                                }

                                if ("INSUFFICIENT_BALANCE".equals(status)) {

                                    final var balance = rs.getBigDecimal("old_balance");

                                    throw new RuntimeException(
                                        new WalletEngine.InsufficientBalanceException(
                                            transactionId, walletId,
                                            amount, balance));
                                }
                            }

                        }

                        hasResults = stm.getMoreResults();
                    }

                    throw new RuntimeException(new WalletEngine.NoBalanceUpdateException(transactionId));
                }

            });
        } catch (final RuntimeException e) {

            if (e.getCause() instanceof WalletEngine.NoBalanceUpdateException e1) {
                throw e1;
            }

            if (e.getCause() instanceof WalletEngine.InsufficientBalanceException e1) {
                throw e1;
            }

            throw e;
        }
    }

}
