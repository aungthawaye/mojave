package org.mojave.core.wallet.engine.mysql.task;

import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.wallet.WalletId;
import org.mojave.core.wallet.contract.engine.WalletEngine;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

public final class CreateWalletTask {

    private CreateWalletTask() {

    }

    public static void execute(final JdbcTemplate jdbcTemplate,
                               final WalletId walletId,
                               final Currency currency,
                               final int scale,
                               final String tag)
        throws WalletEngine.WalletIdAlreadyTakenException {

        try {
            jdbcTemplate.execute((ConnectionCallback<Void>) con -> {
                try (var stm = con.prepareStatement("CALL sp_create_wallet(?, ?, ?, ?)")) {

                    stm.setLong(1, walletId.getId());
                    stm.setString(2, currency.name());
                    stm.setInt(3, scale);
                    stm.setString(4, tag);

                    var hasResults = stm.execute();

                    while (hasResults) {
                        try (var rs = stm.getResultSet()) {
                            if (rs != null && rs.next()) {
                                final var status = rs.getString("status");

                                if ("SUCCESS".equals(status)) {
                                    return null;
                                }

                                if ("WALLET_ALREADY_EXISTS".equals(status)) {
                                    throw new RuntimeException(
                                        new WalletEngine.WalletIdAlreadyTakenException(walletId));
                                }
                            }
                        }

                        hasResults = stm.getMoreResults();
                    }

                    throw new RuntimeException("No create wallet result returned for walletId: " + walletId);
                }
            });

        } catch (final RuntimeException e) {

            if (e.getCause() instanceof WalletEngine.WalletIdAlreadyTakenException e1) {
                throw e1;
            }

            throw e;
        }
    }

}
