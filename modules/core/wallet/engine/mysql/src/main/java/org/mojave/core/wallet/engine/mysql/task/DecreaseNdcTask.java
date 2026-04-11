package org.mojave.core.wallet.engine.mysql.task;

import org.mojave.common.datatype.enums.wallet.PositionAction;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.common.datatype.identifier.wallet.NdcUpdateId;
import org.mojave.common.datatype.identifier.wallet.WalletId;
import org.mojave.core.wallet.contract.engine.WalletEngine;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.Instant;

public final class DecreaseNdcTask {

    private DecreaseNdcTask() {

    }

    public static WalletEngine.NdcHistory execute(final JdbcTemplate jdbcTemplate,
                                                  final NdcUpdateId ndcUpdateId,
                                                  final TransactionId transactionId,
                                                  final Instant transactionAt,
                                                  final WalletId walletId,
                                                  final BigDecimal amount,
                                                  final String description)
        throws WalletEngine.NoBalanceUpdateException,
               WalletEngine.PositionReservedExceedsNdcException {

        try {
            return jdbcTemplate.execute((ConnectionCallback<WalletEngine.NdcHistory>) con -> {
                try (var stm = con.prepareStatement("CALL sp_decrease_ndc(?, ?, ?, ?, ?, ?)")) {

                    stm.setLong(1, ndcUpdateId.getId());
                    stm.setLong(2, transactionId.getId());
                    stm.setLong(3, transactionAt.getEpochSecond());
                    stm.setLong(4, walletId.getId());
                    stm.setBigDecimal(5, amount);
                    stm.setString(6, description);

                    var hasResults = stm.execute();

                    while (hasResults) {
                        try (var rs = stm.getResultSet()) {
                            if (rs != null && rs.next()) {
                                final var status = rs.getString("status");

                                if ("SUCCESS".equals(status)) {
                                    return WalletTaskMapper.mapNdcHistory(rs, PositionAction.DECREASE);
                                }

                                if ("POSITION_RESERVED_EXCEEDS_NDC".equals(status)) {
                                    throw new RuntimeException(
                                        new WalletEngine.PositionReservedExceedsNdcException(
                                            walletId, amount, rs.getBigDecimal("position"),
                                            rs.getBigDecimal("reserved"),
                                            rs.getBigDecimal("new_ndc"), transactionId));
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

            if (e.getCause() instanceof WalletEngine.PositionReservedExceedsNdcException e1) {
                throw e1;
            }

            throw e;
        }
    }

}
