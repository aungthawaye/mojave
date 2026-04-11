package org.mojave.wallet.engine.mysql.task;

import org.mojave.common.datatype.identifier.wallet.PositionUpdateId;
import org.mojave.wallet.contract.engine.WalletEngine;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

public final class RollbackPositionReservationTask {

    private RollbackPositionReservationTask() {

    }

    public static WalletEngine.PositionHistory execute(final JdbcTemplate jdbcTemplate,
                                                       final PositionUpdateId nextPositionUpdateId,
                                                       final PositionUpdateId reservationId)
        throws WalletEngine.PositionReservationRollbackFailedException {

        try {
            return jdbcTemplate.execute((ConnectionCallback<WalletEngine.PositionHistory>) con -> {
                try (var stm = con.prepareStatement("CALL sp_rollback_position(?, ?)")) {

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

                                if ("ROLLBACK_FAILED".equals(status)) {

                                    throw new RuntimeException(
                                        new WalletEngine.PositionReservationRollbackFailedException(
                                            reservationId));
                                }
                            }
                        }
                        hasResults = stm.getMoreResults();
                    }

                    throw new RuntimeException(
                        new WalletEngine.PositionReservationRollbackFailedException(reservationId));
                }
            });
        } catch (final RuntimeException e) {

            if (e.getCause() instanceof WalletEngine.PositionReservationRollbackFailedException e1) {
                throw e1;
            }

            throw e;
        }
    }

}
