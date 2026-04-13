package org.mojave.core.wallet.engine.mysql.task;

import org.mojave.scheme.rule.identifier.transaction.TransactionId;
import org.mojave.scheme.rule.identifier.wallet.BalanceUpdateId;
import org.mojave.scheme.rule.identifier.wallet.WalletId;
import org.mojave.core.wallet.contract.engine.WalletEngine;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.Instant;

public final class DepositBalanceTask {

    private DepositBalanceTask() {

    }

    public static WalletEngine.BalanceHistory execute(final JdbcTemplate jdbcTemplate,
                                                      final TransactionId transactionId,
                                                      final Instant transactionAt,
                                                      final WalletId walletId,
                                                      final BigDecimal amount,
                                                      final String description,
                                                      final BalanceUpdateId nextBalanceUpdateId)
        throws WalletEngine.NoBalanceUpdateException {

        try {

            return jdbcTemplate.execute((ConnectionCallback<WalletEngine.BalanceHistory>) con -> {

                try (var stm = con.prepareStatement("CALL sp_deposit_balance(?, ?, ?, ?, ?, ?)")) {

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

                                final var status = rs.getString("result");

                                if ("SUCCESS".equals(status)) {
                                    return WalletTaskMapper.mapBalanceHistory(rs, null);
                                }
                            }

                        }

                        hasResults = stm.getMoreResults();
                    }

                    throw new RuntimeException(
                        new WalletEngine.NoBalanceUpdateException(transactionId));
                }
            });
        } catch (final RuntimeException e) {

            if (e.getCause() instanceof WalletEngine.NoBalanceUpdateException e1) {
                throw e1;
            }

            throw e;
        }
    }

}
