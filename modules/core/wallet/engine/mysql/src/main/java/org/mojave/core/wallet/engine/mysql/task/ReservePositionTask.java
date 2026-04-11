package org.mojave.core.wallet.engine.mysql.task;

import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.common.datatype.identifier.wallet.PositionUpdateId;
import org.mojave.common.datatype.identifier.wallet.WalletId;
import org.mojave.core.wallet.contract.engine.WalletEngine;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.Instant;

public final class ReservePositionTask {

    private ReservePositionTask() {

    }

    public static WalletEngine.PositionHistory execute(final JdbcTemplate jdbcTemplate,
                                                       final PositionUpdateId nextPositionUpdateId,
                                                       final TransactionId transactionId,
                                                       final Instant transactionAt,
                                                       final WalletId walletId,
                                                       final BigDecimal amount,
                                                       final String description)
        throws WalletEngine.NoPositionUpdateException, WalletEngine.PositionLimitExceededException {

        try {
            return jdbcTemplate.execute((ConnectionCallback<WalletEngine.PositionHistory>) con -> {
                try (var stm = con.prepareStatement("CALL sp_reserve_position(?, ?, ?, ?, ?, ?)")) {

                    stm.setLong(1, transactionId.getId());
                    stm.setLong(2, transactionAt.getEpochSecond());
                    stm.setLong(3, nextPositionUpdateId.getId());
                    stm.setLong(4, walletId.getId());
                    stm.setBigDecimal(5, amount);
                    stm.setString(6, description);

                    var hasResults = stm.execute();

                    while (hasResults) {
                        try (var rs = stm.getResultSet()) {
                            if (rs != null && rs.next()) {

                                final var status = rs.getString("status");

                                if ("SUCCESS".equals(status)) {

                                    return WalletTaskMapper.mapPositionHistory(rs);
                                }

                                if ("LIMIT_EXCEEDED".equals(status)) {

                                    throw new RuntimeException(new WalletEngine.PositionLimitExceededException(
                                        walletId, amount, rs.getBigDecimal("old_position"),
                                        rs.getBigDecimal("old_reserved"), rs.getBigDecimal("ndc"),
                                        transactionId));
                                }
                            }
                        }

                        hasResults = stm.getMoreResults();
                    }

                    throw new RuntimeException(new WalletEngine.NoPositionUpdateException(transactionId));
                }
            });
        } catch (final RuntimeException e) {
            if (e.getCause() instanceof WalletEngine.NoPositionUpdateException e1) {
                throw e1;
            }
            if (e.getCause() instanceof WalletEngine.PositionLimitExceededException e1) {
                throw e1;
            }
            throw e;
        }
    }

}
