package org.mojave.core.wallet.engine.mysql.task;

import org.mojave.common.datatype.identifier.wallet.PositionUpdateId;
import org.mojave.core.wallet.contract.engine.WalletEngine;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

public final class CommitPositionReservationTask {

    private CommitPositionReservationTask() {

    }

    public static WalletEngine.PositionHistory execute(final JdbcTemplate jdbcTemplate,
                                                       final PositionUpdateId nextPositionUpdateId,
                                                       final PositionUpdateId reservationId)
        throws WalletEngine.PositionReservationCommitFailedException {

        try {
            return jdbcTemplate.execute((ConnectionCallback<WalletEngine.PositionHistory>) con -> {
                try (var stm = con.prepareStatement("CALL sp_commit_position(?, ?)")) {

                    stm.setLong(1, reservationId.getId());
                    stm.setLong(2, nextPositionUpdateId.getId());

                    var hasResults = stm.execute();

                    while (hasResults) {
                        try (var rs = stm.getResultSet()) {
                            if (rs != null && rs.next()) {
                                final var status = rs.getString("status");

                                if ("SUCCESS".equals(status)) {
                                    return WalletTaskMapper.mapPositionHistory(rs);
                                }

                                if ("COMMIT_FAILED".equals(status)) {
                                    throw new RuntimeException(
                                        new WalletEngine.PositionReservationCommitFailedException(
                                            reservationId));
                                }
                            }
                        }

                        hasResults = stm.getMoreResults();
                    }

                    throw new RuntimeException(
                        new WalletEngine.PositionReservationCommitFailedException(reservationId));
                }
            });

        } catch (final RuntimeException e) {

            if (e.getCause() instanceof WalletEngine.PositionReservationCommitFailedException e1) {
                throw e1;
            }

            throw e;
        }
    }

}
