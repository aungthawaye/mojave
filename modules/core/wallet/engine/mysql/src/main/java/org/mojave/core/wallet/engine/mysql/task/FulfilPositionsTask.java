package org.mojave.core.wallet.engine.mysql.task;

import org.mojave.scheme.rule.identifier.wallet.PositionUpdateId;
import org.mojave.scheme.rule.identifier.wallet.WalletId;
import org.mojave.core.wallet.contract.engine.WalletEngine;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

public final class FulfilPositionsTask {

    private FulfilPositionsTask() {

    }

    public static WalletEngine.FulfilResult execute(final JdbcTemplate jdbcTemplate,
                                                    final PositionUpdateId reservationId,
                                                    final PositionUpdateId reservationCommitId,
                                                    final PositionUpdateId positionDecrementId,
                                                    final WalletId payeeWalletId,
                                                    final String description)
        throws WalletEngine.NoPositionFulfilmentException {

        try {

            return jdbcTemplate.execute((ConnectionCallback<WalletEngine.FulfilResult>) con -> {

                try (var stm = con.prepareStatement("CALL sp_fulfil_positions(?, ?, ?, ?, ?)")) {

                    stm.setLong(1, reservationId.getId());
                    stm.setLong(2, reservationCommitId.getId());
                    stm.setLong(3, positionDecrementId.getId());
                    stm.setLong(4, payeeWalletId.getId());
                    stm.setString(5, description);

                    var hasResults = stm.execute();

                    while (hasResults) {
                        try (var rs = stm.getResultSet()) {
                            if (rs != null && rs.next()) {
                                final var status = rs.getString("status");

                                if ("SUCCESS".equals(status)) {

                                    final var payerCommitId = new PositionUpdateId(
                                        rs.getLong("payer_commit_id"));

                                    final var payeeCommitId = new PositionUpdateId(
                                        rs.getLong("payee_commit_id"));

                                    return new WalletEngine.FulfilResult(payerCommitId, payeeCommitId);
                                }

                                throw new RuntimeException(
                                    new WalletEngine.NoPositionFulfilmentException(reservationId));
                            }
                        }
                        hasResults = stm.getMoreResults();
                    }

                    throw new RuntimeException(
                        "No fulfil result returned for reservationId: " + reservationId);
                }
            });

        } catch (final RuntimeException e) {

            if (e.getCause() instanceof WalletEngine.NoPositionFulfilmentException e1) {
                throw e1;
            }

            throw e;
        }
    }

}
