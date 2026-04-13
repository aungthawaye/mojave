package org.mojave.core.wallet.engine.mysql.task;

import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.enums.wallet.BalanceAction;
import org.mojave.scheme.rule.enums.wallet.PositionAction;
import org.mojave.scheme.rule.identifier.transaction.TransactionId;
import org.mojave.scheme.rule.identifier.wallet.BalanceUpdateId;
import org.mojave.scheme.rule.identifier.wallet.NdcUpdateId;
import org.mojave.scheme.rule.identifier.wallet.PositionUpdateId;
import org.mojave.scheme.rule.identifier.wallet.WalletId;
import org.mojave.core.wallet.contract.engine.WalletEngine;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

final class WalletTaskMapper {

    private WalletTaskMapper() {

    }

    static WalletEngine.BalanceHistory mapBalanceHistory(final ResultSet rs,
                                                         final BalanceUpdateId reversalId)
        throws SQLException {

        final var currency = rs.getString("currency");

        if (currency == null) {
            throw new IllegalStateException(
                "Currency is null in balance history result for balance_update_id: " +
                    rs.getLong("balance_update_id"));
        }

        return new WalletEngine.BalanceHistory(
            new BalanceUpdateId(rs.getLong("balance_update_id")),
            new WalletId(rs.getLong("balance_id")),
            BalanceAction.valueOf(rs.getString("action")),
            new TransactionId(rs.getLong("transaction_id")),
            Currency.valueOf(currency),
            rs.getBigDecimal("amount"),
            rs.getBigDecimal("old_balance"),
            rs.getBigDecimal("new_balance"),
            Instant.ofEpochSecond(rs.getLong("transaction_at")),
            reversalId);
    }

    static WalletEngine.PositionHistory mapPositionHistory(final ResultSet rs) throws SQLException {

        return new WalletEngine.PositionHistory(
            new PositionUpdateId(rs.getLong("position_update_id")),
            new WalletId(rs.getLong("position_id")),
            PositionAction.valueOf(rs.getString("action")),
            new TransactionId(rs.getLong("transaction_id")),
            Currency.valueOf(rs.getString("currency")),
            rs.getBigDecimal("amount"),
            rs.getBigDecimal("old_position"),
            rs.getBigDecimal("new_position"),
            rs.getBigDecimal("old_reserved"),
            rs.getBigDecimal("new_reserved"),
            rs.getBigDecimal("ndc"),
            Instant.ofEpochSecond(rs.getLong("transaction_at")));
    }

    static WalletEngine.NdcHistory mapNdcHistory(final ResultSet rs) throws SQLException {

        return new WalletEngine.NdcHistory(
            new NdcUpdateId(rs.getLong("ndc_update_id")),
            new WalletId(rs.getLong("wallet_id")),
            PositionAction.valueOf(rs.getString("action")),
            new TransactionId(rs.getLong("transaction_id")),
            Currency.valueOf(rs.getString("currency")),
            rs.getBigDecimal("amount"),
            rs.getBigDecimal("old_ndc"),
            rs.getBigDecimal("new_ndc"),
            Instant.ofEpochSecond(rs.getLong("transaction_at")));
    }

}
