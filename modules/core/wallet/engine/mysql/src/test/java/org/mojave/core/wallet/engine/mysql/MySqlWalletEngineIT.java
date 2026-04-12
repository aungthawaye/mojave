package org.mojave.core.wallet.engine.mysql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.enums.wallet.BalanceAction;
import org.mojave.scheme.rule.enums.wallet.PositionAction;
import org.mojave.scheme.rule.identifier.transaction.TransactionId;
import org.mojave.scheme.rule.identifier.wallet.BalanceUpdateId;
import org.mojave.scheme.rule.identifier.wallet.NdcUpdateId;
import org.mojave.scheme.rule.identifier.wallet.PositionUpdateId;
import org.mojave.scheme.rule.identifier.wallet.WalletId;
import org.mojave.core.wallet.contract.engine.WalletEngine;
import org.mojave.core.wallet.engine.mysql.task.CommitPositionReservationTask;
import org.mojave.core.wallet.engine.mysql.task.CreateWalletTask;
import org.mojave.core.wallet.engine.mysql.task.DecreaseNdcTask;
import org.mojave.core.wallet.engine.mysql.task.DecreasePositionTask;
import org.mojave.core.wallet.engine.mysql.task.DepositBalanceTask;
import org.mojave.core.wallet.engine.mysql.task.FulfilPositionsTask;
import org.mojave.core.wallet.engine.mysql.task.IncreaseNdcTask;
import org.mojave.core.wallet.engine.mysql.task.IncreasePositionTask;
import org.mojave.core.wallet.engine.mysql.task.RefundBalanceTask;
import org.mojave.core.wallet.engine.mysql.task.ReservePositionTask;
import org.mojave.core.wallet.engine.mysql.task.RollbackPositionReservationTask;
import org.mojave.core.wallet.engine.mysql.task.WithdrawBalanceTask;
import org.mockito.MockedStatic;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

public class MySqlWalletEngineIT {

    private static final Instant TRANSACTION_AT = Instant.parse("2026-01-12T09:10:11Z");

    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {

        this.jdbcTemplate = mock(JdbcTemplate.class);
    }

    @Test
    public void commitPositionReservation_shouldDelegateToTask() throws Exception {

        final var walletEngine = this.walletEngine();
        final var nextPositionUpdateId = new PositionUpdateId(101L);
        final var reservationId = new PositionUpdateId(102L);
        final var expected = this.positionHistory(
            new PositionUpdateId(103L), new WalletId(104L), PositionAction.COMMIT,
            new TransactionId(105L), Currency.USD, new BigDecimal("10.00"),
            new BigDecimal("50.00"), new BigDecimal("50.00"), new BigDecimal("10.00"),
            BigDecimal.ZERO, new BigDecimal("100.00"), TRANSACTION_AT);

        try (final MockedStatic<CommitPositionReservationTask> mocked =
                 mockStatic(CommitPositionReservationTask.class)) {

            mocked.when(
                () -> CommitPositionReservationTask.execute(
                    this.jdbcTemplate, nextPositionUpdateId, reservationId))
                .thenReturn(expected);

            final var actual = walletEngine.commitPositionReservation(
                nextPositionUpdateId, reservationId);

            assertSame(expected, actual);
        }
    }

    @Test
    public void createWallet_shouldDelegateToTask() throws Exception {

        final var walletEngine = this.walletEngine();
        final var walletId = new WalletId(201L);
        final var currency = Currency.USD;
        final var scale = 2;
        final var tag = "P2P_TRANSFER";

        try (final MockedStatic<CreateWalletTask> mocked = mockStatic(CreateWalletTask.class)) {

            mocked.when(
                () -> CreateWalletTask.execute(
                    this.jdbcTemplate, walletId, currency, scale, tag))
                .thenAnswer(invocation -> null);

            walletEngine.createWallet(walletId, currency, scale, tag);

            mocked.verify(
                () -> CreateWalletTask.execute(
                    this.jdbcTemplate, walletId, currency, scale, tag));
        }
    }

    @Test
    public void decreaseNdc_shouldDelegateToTask() throws Exception {

        final var walletEngine = this.walletEngine();
        final var ndcUpdateId = new NdcUpdateId(301L);
        final var transactionId = new TransactionId(302L);
        final var walletId = new WalletId(303L);
        final var amount = new BigDecimal("4.50");
        final var description = "Decrease NDC";
        final var expected = this.ndcHistory(
            ndcUpdateId, walletId, PositionAction.DECREASE, transactionId, Currency.USD,
            amount, new BigDecimal("100.00"), new BigDecimal("95.50"), TRANSACTION_AT);

        try (final MockedStatic<DecreaseNdcTask> mocked = mockStatic(DecreaseNdcTask.class)) {

            mocked.when(
                () -> DecreaseNdcTask.execute(
                    this.jdbcTemplate, ndcUpdateId, transactionId, TRANSACTION_AT, walletId,
                    amount, description))
                .thenReturn(expected);

            final var actual = walletEngine.decreaseNdc(
                ndcUpdateId, transactionId, TRANSACTION_AT, walletId, amount, description);

            assertSame(expected, actual);
        }
    }

    @Test
    public void decreasePosition_shouldDelegateToTask() throws Exception {

        final var walletEngine = this.walletEngine();
        final var nextPositionUpdateId = new PositionUpdateId(401L);
        final var transactionId = new TransactionId(402L);
        final var walletId = new WalletId(403L);
        final var amount = new BigDecimal("8.00");
        final var description = "Decrease Position";
        final var expected = this.positionHistory(
            nextPositionUpdateId, walletId, PositionAction.DECREASE, transactionId,
            Currency.USD, amount, new BigDecimal("20.00"), new BigDecimal("12.00"),
            BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("100.00"), TRANSACTION_AT);

        try (final MockedStatic<DecreasePositionTask> mocked =
                 mockStatic(DecreasePositionTask.class)) {

            mocked.when(
                () -> DecreasePositionTask.execute(
                    this.jdbcTemplate, nextPositionUpdateId, transactionId, TRANSACTION_AT,
                    walletId, amount, description))
                .thenReturn(expected);

            final var actual = walletEngine.decreasePosition(
                nextPositionUpdateId, transactionId, TRANSACTION_AT, walletId, amount,
                description);

            assertSame(expected, actual);
        }
    }

    @Test
    public void depositBalance_shouldDelegateToTask() throws Exception {

        final var walletEngine = this.walletEngine();
        final var transactionId = new TransactionId(501L);
        final var walletId = new WalletId(502L);
        final var amount = new BigDecimal("12.25");
        final var description = "Deposit Balance";
        final var nextBalanceUpdateId = new BalanceUpdateId(503L);
        final var expected = this.balanceHistory(
            nextBalanceUpdateId, walletId, BalanceAction.DEPOSIT, transactionId, Currency.USD,
            amount, new BigDecimal("7.75"), new BigDecimal("20.00"), TRANSACTION_AT, null);

        try (final MockedStatic<DepositBalanceTask> mocked = mockStatic(DepositBalanceTask.class)) {

            mocked.when(
                () -> DepositBalanceTask.execute(
                    this.jdbcTemplate, transactionId, TRANSACTION_AT, walletId, amount,
                    description, nextBalanceUpdateId))
                .thenReturn(expected);

            final var actual = walletEngine.depositBalance(
                transactionId, TRANSACTION_AT, walletId, amount, description,
                nextBalanceUpdateId);

            assertSame(expected, actual);
        }
    }

    @Test
    public void fulfil_shouldDelegateToTask() throws Exception {

        final var walletEngine = this.walletEngine();
        final var reservationId = new PositionUpdateId(601L);
        final var reservationCommitId = new PositionUpdateId(602L);
        final var positionDecrementId = new PositionUpdateId(603L);
        final var payeeWalletId = new WalletId(604L);
        final var description = "Fulfil Positions";
        final var expected = new WalletEngine.FulfilResult(
            new PositionUpdateId(605L), new PositionUpdateId(606L));

        try (final MockedStatic<FulfilPositionsTask> mocked = mockStatic(FulfilPositionsTask.class)) {

            mocked.when(
                () -> FulfilPositionsTask.execute(
                    this.jdbcTemplate, reservationId, reservationCommitId, positionDecrementId,
                    payeeWalletId, description))
                .thenReturn(expected);

            final var actual = walletEngine.fulfil(
                reservationId, reservationCommitId, positionDecrementId, payeeWalletId,
                description);

            assertSame(expected, actual);
        }
    }

    @Test
    public void increaseNdc_shouldDelegateToTask() throws Exception {

        final var walletEngine = this.walletEngine();
        final var ndcUpdateId = new NdcUpdateId(701L);
        final var transactionId = new TransactionId(702L);
        final var walletId = new WalletId(703L);
        final var amount = new BigDecimal("9.75");
        final var description = "Increase NDC";
        final var expected = this.ndcHistory(
            ndcUpdateId, walletId, PositionAction.INCREASE, transactionId, Currency.USD,
            amount, new BigDecimal("20.00"), new BigDecimal("29.75"), TRANSACTION_AT);

        try (final MockedStatic<IncreaseNdcTask> mocked = mockStatic(IncreaseNdcTask.class)) {

            mocked.when(
                () -> IncreaseNdcTask.execute(
                    this.jdbcTemplate, ndcUpdateId, transactionId, TRANSACTION_AT, walletId,
                    amount, description))
                .thenReturn(expected);

            final var actual = walletEngine.increaseNdc(
                ndcUpdateId, transactionId, TRANSACTION_AT, walletId, amount, description);

            assertSame(expected, actual);
        }
    }

    @Test
    public void increasePosition_shouldDelegateToTask() throws Exception {

        final var walletEngine = this.walletEngine();
        final var nextPositionUpdateId = new PositionUpdateId(801L);
        final var transactionId = new TransactionId(802L);
        final var walletId = new WalletId(803L);
        final var amount = new BigDecimal("3.00");
        final var description = "Increase Position";
        final var expected = this.positionHistory(
            nextPositionUpdateId, walletId, PositionAction.INCREASE, transactionId,
            Currency.USD, amount, new BigDecimal("2.00"), new BigDecimal("5.00"),
            BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("100.00"), TRANSACTION_AT);

        try (final MockedStatic<IncreasePositionTask> mocked =
                 mockStatic(IncreasePositionTask.class)) {

            mocked.when(
                () -> IncreasePositionTask.execute(
                    this.jdbcTemplate, nextPositionUpdateId, transactionId, TRANSACTION_AT,
                    walletId, amount, description))
                .thenReturn(expected);

            final var actual = walletEngine.increasePosition(
                nextPositionUpdateId, transactionId, TRANSACTION_AT, walletId, amount,
                description);

            assertSame(expected, actual);
        }
    }

    @Test
    public void refundBalance_shouldDelegateToTask() throws Exception {

        final var walletEngine = this.walletEngine();
        final var reversalId = new BalanceUpdateId(901L);
        final var nextBalanceUpdateId = new BalanceUpdateId(902L);
        final var expected = this.balanceHistory(
            nextBalanceUpdateId, new WalletId(903L), BalanceAction.REVERSE_WITHDRAW,
            new TransactionId(904L), Currency.USD, new BigDecimal("5.00"),
            new BigDecimal("10.00"), new BigDecimal("15.00"), TRANSACTION_AT, reversalId);

        try (final MockedStatic<RefundBalanceTask> mocked = mockStatic(RefundBalanceTask.class)) {

            mocked.when(
                () -> RefundBalanceTask.execute(
                    this.jdbcTemplate, reversalId, nextBalanceUpdateId))
                .thenReturn(expected);

            final var actual = walletEngine.refundBalance(reversalId, nextBalanceUpdateId);

            assertSame(expected, actual);
        }
    }

    @Test
    public void reservePosition_shouldDelegateToTask() throws Exception {

        final var walletEngine = this.walletEngine();
        final var nextPositionUpdateId = new PositionUpdateId(1001L);
        final var transactionId = new TransactionId(1002L);
        final var walletId = new WalletId(1003L);
        final var amount = new BigDecimal("6.00");
        final var description = "Reserve Position";
        final var expected = this.positionHistory(
            nextPositionUpdateId, walletId, PositionAction.RESERVE, transactionId,
            Currency.USD, amount, new BigDecimal("20.00"), new BigDecimal("20.00"),
            new BigDecimal("1.00"), new BigDecimal("7.00"), new BigDecimal("100.00"),
            TRANSACTION_AT);

        try (final MockedStatic<ReservePositionTask> mocked = mockStatic(ReservePositionTask.class)) {

            mocked.when(
                () -> ReservePositionTask.execute(
                    this.jdbcTemplate, nextPositionUpdateId, transactionId, TRANSACTION_AT,
                    walletId, amount, description))
                .thenReturn(expected);

            final var actual = walletEngine.reservePosition(
                nextPositionUpdateId, transactionId, TRANSACTION_AT, walletId, amount,
                description);

            assertSame(expected, actual);
        }
    }

    @Test
    public void rollbackPositionReservation_shouldDelegateToTask() throws Exception {

        final var walletEngine = this.walletEngine();
        final var nextPositionUpdateId = new PositionUpdateId(1101L);
        final var reservationId = new PositionUpdateId(1102L);
        final var expected = this.positionHistory(
            nextPositionUpdateId, new WalletId(1103L), PositionAction.ROLLBACK,
            new TransactionId(1104L), Currency.USD, new BigDecimal("4.00"),
            new BigDecimal("20.00"), new BigDecimal("20.00"), new BigDecimal("4.00"),
            BigDecimal.ZERO, new BigDecimal("100.00"), TRANSACTION_AT);

        try (final MockedStatic<RollbackPositionReservationTask> mocked =
                 mockStatic(RollbackPositionReservationTask.class)) {

            mocked.when(
                () -> RollbackPositionReservationTask.execute(
                    this.jdbcTemplate, nextPositionUpdateId, reservationId))
                .thenReturn(expected);

            final var actual = walletEngine.rollbackPositionReservation(
                nextPositionUpdateId, reservationId);

            assertSame(expected, actual);
        }
    }

    @Test
    public void withdrawBalance_shouldDelegateToTask() throws Exception {

        final var walletEngine = this.walletEngine();
        final var nextBalanceUpdateId = new BalanceUpdateId(1201L);
        final var transactionId = new TransactionId(1202L);
        final var walletId = new WalletId(1203L);
        final var amount = new BigDecimal("2.50");
        final var description = "Withdraw Balance";
        final var expected = this.balanceHistory(
            nextBalanceUpdateId, walletId, BalanceAction.WITHDRAW, transactionId,
            Currency.USD, amount, new BigDecimal("10.00"), new BigDecimal("7.50"),
            TRANSACTION_AT, null);

        try (final MockedStatic<WithdrawBalanceTask> mocked =
                 mockStatic(WithdrawBalanceTask.class)) {

            mocked.when(
                () -> WithdrawBalanceTask.execute(
                    this.jdbcTemplate, nextBalanceUpdateId, transactionId, TRANSACTION_AT,
                    walletId, amount, description))
                .thenReturn(expected);

            final var actual = walletEngine.withdrawBalance(
                nextBalanceUpdateId, transactionId, TRANSACTION_AT, walletId, amount,
                description);

            assertSame(expected, actual);
        }
    }

    private WalletEngine.BalanceHistory balanceHistory(final BalanceUpdateId balanceUpdateId,
                                                       final WalletId walletId,
                                                       final BalanceAction action,
                                                       final TransactionId transactionId,
                                                       final Currency currency,
                                                       final BigDecimal amount,
                                                       final BigDecimal oldBalance,
                                                       final BigDecimal newBalance,
                                                       final Instant transactionAt,
                                                       final BalanceUpdateId reversalId) {

        return new WalletEngine.BalanceHistory(
            balanceUpdateId, walletId, action, transactionId, currency, amount, oldBalance,
            newBalance, transactionAt, reversalId);
    }

    private WalletEngine.NdcHistory ndcHistory(final NdcUpdateId ndcUpdateId,
                                               final WalletId walletId,
                                               final PositionAction action,
                                               final TransactionId transactionId,
                                               final Currency currency,
                                               final BigDecimal amount,
                                               final BigDecimal oldNdc,
                                               final BigDecimal newNdc,
                                               final Instant transactionAt) {

        return new WalletEngine.NdcHistory(
            ndcUpdateId, walletId, action, transactionId, currency, amount, oldNdc, newNdc,
            transactionAt);
    }

    private WalletEngine.PositionHistory positionHistory(final PositionUpdateId positionUpdateId,
                                                         final WalletId walletId,
                                                         final PositionAction action,
                                                         final TransactionId transactionId,
                                                         final Currency currency,
                                                         final BigDecimal amount,
                                                         final BigDecimal oldPosition,
                                                         final BigDecimal newPosition,
                                                         final BigDecimal oldReserved,
                                                         final BigDecimal newReserved,
                                                         final BigDecimal netDebitCap,
                                                         final Instant transactionAt) {

        return new WalletEngine.PositionHistory(
            positionUpdateId, walletId, action, transactionId, currency, amount, oldPosition,
            newPosition, oldReserved, newReserved, netDebitCap, transactionAt);
    }

    private MySqlWalletEngine walletEngine() throws Exception {

        final var walletEngine = mock(MySqlWalletEngine.class);

        doCallRealMethod().when(walletEngine).commitPositionReservation(any(), any());
        doCallRealMethod().when(walletEngine).createWallet(any(), any(), anyInt(), any());
        doCallRealMethod().when(walletEngine).decreaseNdc(
            any(), any(), any(), any(), any(), anyString());
        doCallRealMethod().when(walletEngine).decreasePosition(
            any(), any(), any(), any(), any(), anyString());
        doCallRealMethod().when(walletEngine).depositBalance(
            any(), any(), any(), any(), anyString(), any());
        doCallRealMethod().when(walletEngine).fulfil(
            any(), any(), any(), any(), anyString());
        doCallRealMethod().when(walletEngine).increaseNdc(
            any(), any(), any(), any(), any(), anyString());
        doCallRealMethod().when(walletEngine).increasePosition(
            any(), any(), any(), any(), any(), anyString());
        doCallRealMethod().when(walletEngine).refundBalance(any(), any());
        doCallRealMethod().when(walletEngine).reservePosition(
            any(), any(), any(), any(), any(), anyString());
        doCallRealMethod().when(walletEngine).rollbackPositionReservation(any(), any());
        doCallRealMethod().when(walletEngine).withdrawBalance(
            any(), any(), any(), any(), any(), anyString());

        final Field field = MySqlWalletEngine.class.getDeclaredField("jdbcTemplate");

        field.setAccessible(true);
        field.set(walletEngine, this.jdbcTemplate);

        return walletEngine;
    }

}
