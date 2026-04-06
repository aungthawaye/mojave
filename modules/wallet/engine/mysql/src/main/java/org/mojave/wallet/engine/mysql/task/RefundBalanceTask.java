package org.mojave.wallet.engine.mysql.task;

import org.mojave.common.datatype.identifier.wallet.BalanceUpdateId;
import org.mojave.wallet.contract.engine.WalletEngine;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

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

                                    final var withdrawId = new BalanceUpdateId(
                                        rs.getLong("withdraw_id"));

                                    return WalletTaskMapper.mapBalanceHistory(rs, withdrawId);
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

}
