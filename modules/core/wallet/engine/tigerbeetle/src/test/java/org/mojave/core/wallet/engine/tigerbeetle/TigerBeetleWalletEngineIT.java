package org.mojave.core.wallet.engine.tigerbeetle;

import com.tigerbeetle.Client;
import com.tigerbeetle.IdBatch;
import com.tigerbeetle.UInt128;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.mojave.core.wallet.contract.engine.WalletEngine;
import org.mojave.core.wallet.engine.tigerbeetle.store.WalletCodec;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.identifier.transaction.TransactionId;
import org.mojave.scheme.rule.identifier.wallet.BalanceUpdateId;
import org.mojave.scheme.rule.identifier.wallet.NdcUpdateId;
import org.mojave.scheme.rule.identifier.wallet.PositionUpdateId;
import org.mojave.scheme.rule.identifier.wallet.WalletId;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnabledIfSystemProperty(named = "tigerbeetle.addresses", matches = ".+")
@Timeout(60)
class TigerBeetleWalletEngineIT {

    private static final AtomicLong IDS = new AtomicLong(System.currentTimeMillis() * 100_000);

    private static final Instant AT = Instant.parse("2026-01-02T03:04:05.123456789Z");

    private TigerBeetleWalletEngine engine;

    @BeforeEach
    void setUp() {

        this.engine = open();
    }

    @AfterEach
    void tearDown() {

        this.engine.close();
    }

    @Test
    void depositsWithdrawalsAndReversalsAreDurableAndIdempotent() throws Exception {

        final var wallet = wallet(Currency.USD);
        final var depositId = balanceId();
        final var transaction = transaction();
        final var deposit = this.engine.depositBalance(transaction, AT, wallet, money("100"), "fund", depositId);
        final var withdrawId = balanceId();
        final var withdraw = this.engine.withdrawBalance(withdrawId, transaction(), AT, wallet, money("30"), "pay");

        assertEquals(money("0"), deposit.oldBalance());
        assertEquals(money("100"), deposit.newBalance());
        assertEquals(money("70"), withdraw.newBalance());
        assertEquals(AT, deposit.transactionAt());
        assertEquals(deposit, this.engine.depositBalance(transaction, AT, wallet, money("100"), "fund", depositId));
        assertThrows(WalletEngine.NoBalanceUpdateException.class,
            () -> this.engine.depositBalance(transaction, AT, wallet, money("101"), "fund", depositId));
        assertThrows(WalletEngine.NoBalanceUpdateException.class,
            () -> this.engine.depositBalance(transaction, AT, wallet, money("100"), "fund", balanceId()));

        final var reversalId = balanceId();
        final var reversal = this.engine.reverseBalanceWithdraw(withdrawId, reversalId);
        assertEquals(money("100"), reversal.newBalance());
        assertEquals(withdrawId, reversal.reversalId());
        assertThrows(WalletEngine.BalanceReversalFailedException.class,
            () -> this.engine.reverseBalanceWithdraw(withdrawId, balanceId()));
        assertThrows(WalletEngine.BalanceReversalFailedException.class,
            () -> this.engine.reverseBalanceWithdraw(depositId, balanceId()));

        try (final var reopened = open()) {
            assertEquals(reversal, reopened.reverseBalanceWithdraw(withdrawId, reversalId));
            assertEquals(deposit, reopened.depositBalance(transaction, AT, wallet, money("100"), "fund", depositId));
        }
        assertNative(wallet, "100", "0", "0", "0");
    }

    @Test
    void reservationsCommitRollbackAndSignedPositionsMatchTheContract() throws Exception {

        final var wallet = funded("100", "80");
        final var reservationId = positionId();
        final var reservation = this.engine.reservePosition(reservationId, transaction(), AT, wallet, money("60"), "hold");

        assertEquals(money("60"), reservation.newReserved());
        assertNative(wallet, "100", "0", "60", "80");
        assertThrows(WalletEngine.PositionLimitExceededException.class,
            () -> this.engine.increasePosition(positionId(), transaction(), AT, wallet, money("21"), null));
        assertThrows(WalletEngine.PositionReservedExceedsNdcException.class,
            () -> this.engine.decreaseNdc(ndcId(), transaction(), AT, wallet, money("21"), null));

        final var commitId = positionId();
        final var committed = this.engine.commitPositionReservation(commitId, reservationId);
        assertEquals(money("60"), committed.newPosition());
        assertEquals(money("0"), committed.newReserved());
        assertEquals(committed, this.engine.commitPositionReservation(commitId, reservationId));
        assertThrows(WalletEngine.PositionReservationRollbackFailedException.class,
            () -> this.engine.rollbackPositionReservation(positionId(), reservationId));

        final var secondId = positionId();
        this.engine.reservePosition(secondId, transaction(), AT, wallet, money("20"), "hold");
        final var rollbackId = positionId();
        final var rolledBack = this.engine.rollbackPositionReservation(rollbackId, secondId);
        assertEquals(money("0"), rolledBack.newReserved());
        assertEquals(rolledBack, this.engine.rollbackPositionReservation(rollbackId, secondId));
        assertThrows(WalletEngine.PositionReservationCommitFailedException.class,
            () -> this.engine.commitPositionReservation(positionId(), secondId));

        final var decrease = this.engine.decreasePosition(positionId(), transaction(), AT, wallet, money("90"), null);
        assertEquals(money("-30"), decrease.newPosition());
        final var increase = this.engine.increasePosition(positionId(), transaction(), AT, wallet, money("10"), null);
        assertEquals(money("-20"), increase.newPosition());
        assertNative(wallet, "100", "-20", "0", "80");
    }

    @Test
    void ndcChecksAndClampingPreserveExistingRules() throws Exception {

        final var wallet = funded("100", "80");
        assertThrows(WalletEngine.BalanceLowerThanNewNdcException.class,
            () -> this.engine.increaseNdc(ndcId(), transaction(), AT, wallet, money("21"), null));

        // Existing MySQL permits withdrawals below NDC; NDC is checked when it is increased.
        this.engine.withdrawBalance(balanceId(), transaction(), AT, wallet, money("90"), null);
        final var decreaseId = ndcId();
        final var tx = transaction();
        final var decrease = this.engine.decreaseNdc(decreaseId, tx, AT, wallet, money("100"), null);
        assertEquals(money("80"), decrease.oldNdc());
        assertEquals(money("0"), decrease.newNdc());
        assertEquals(money("100"), decrease.amount());
        assertEquals(decrease, this.engine.decreaseNdc(decreaseId, tx, AT, wallet, money("100"), null));
        assertNative(wallet, "10", "0", "0", "0");
    }

    @Test
    void fulfilmentIsAtomicAndCannotResolveAReservationTwice() throws Exception {

        final var payer = funded("100", "100");
        final var payee = wallet(Currency.USD);
        final var reservation = positionId();
        this.engine.reservePosition(reservation, transaction(), AT, payer, money("35"), null);
        final var commit = positionId();
        final var decrement = positionId();
        final var result = this.engine.fulfilPositions(reservation, commit, decrement, payee, "settle");

        assertEquals(new WalletEngine.PositionFulfilmentResult(commit, decrement), result);
        assertEquals(result, this.engine.fulfilPositions(reservation, commit, decrement, payee, "settle"));
        assertThrows(WalletEngine.NoPositionFulfilmentException.class,
            () -> this.engine.fulfilPositions(reservation, positionId(), positionId(), payee, "settle"));
        assertNative(payer, "100", "35", "0", "100");
        assertNative(payee, "0", "-35", "0", "0");
    }

    @Test
    void rejectedFulfilmentLeavesReservationPending() throws Exception {

        final var payer = funded("100", "100");
        final var otherCurrency = wallet(Currency.EUR);
        final var payee = wallet(Currency.USD);
        final var tx = transaction();
        final var reservation = positionId();
        this.engine.reservePosition(reservation, tx, AT, payer, money("35"), null);
        assertThrows(WalletEngine.NoPositionFulfilmentException.class,
            () -> this.engine.fulfilPositions(reservation, positionId(), positionId(), otherCurrency, null));

        // Force failure of the payee's unique business key, after a valid payer reservation.
        this.engine.decreasePosition(positionId(), tx, AT, payee, money("1"), null);
        assertThrows(WalletEngine.NoPositionFulfilmentException.class,
            () -> this.engine.fulfilPositions(reservation, positionId(), positionId(), payee, null));
        assertNative(payer, "100", "0", "35", "100");
        assertNative(payee, "0", "-1", "0", "0");
        this.engine.rollbackPositionReservation(positionId(), reservation);
    }

    @Test
    void selfFulfilmentReleasesReservationWithoutChangingNetPosition() throws Exception {

        final var wallet = funded("50", "50");
        final var reservation = positionId();
        this.engine.reservePosition(reservation, transaction(), AT, wallet, money("10"), null);
        this.engine.fulfilPositions(reservation, positionId(), positionId(), wallet, "self");

        assertNative(wallet, "50", "0", "0", "50");
        final var next = this.engine.increasePosition(positionId(), transaction(), AT, wallet, money("5"), null);
        assertEquals(money("0"), next.oldPosition());
        assertEquals(money("0"), next.oldReserved());
    }

    @Test
    void twoClientsCannotOverdrawAndHistoryFormsASerialChain() throws Exception {

        final var wallet = funded("20", "0");
        final var ready = new CountDownLatch(1);
        final var jobs = new ArrayList<Callable<WalletEngine.BalanceHistory>>();
        try (final var other = open(); final var pool = Executors.newFixedThreadPool(8)) {
            for (var index = 0; index < 40; index++) {
                final var writer = index % 2 == 0 ? this.engine : other;
                final var id = balanceId();
                final var tx = transaction();
                jobs.add(() -> {
                    ready.await();
                    try {
                        return writer.withdrawBalance(id, tx, AT, wallet, money("1"), null);
                    } catch (final WalletEngine.InsufficientBalanceException e) {
                        return null;
                    }
                });
            }
            ready.countDown();
            final var histories = new ArrayList<WalletEngine.BalanceHistory>();
            for (final var result : pool.invokeAll(jobs)) {
                final var history = result.get();
                if (history != null) {
                    histories.add(history);
                }
            }
            assertEquals(20, histories.size());
            assertEquals(20, histories.stream().map(WalletEngine.BalanceHistory::oldBalance).distinct().count());
            assertTrue(histories.stream().allMatch(history -> history.newBalance().signum() >= 0));
        }
        assertNative(wallet, "0", "0", "0", "0");
    }

    @Test
    void twoClientsCannotReserveBeyondNdc() throws Exception {

        final var wallet = funded("10", "10");
        try (final var other = open(); final var pool = Executors.newFixedThreadPool(8)) {
            final var jobs = new ArrayList<Callable<Boolean>>();
            for (var index = 0; index < 20; index++) {
                final var writer = index % 2 == 0 ? this.engine : other;
                final var id = positionId();
                final var tx = transaction();
                jobs.add(() -> {
                    try {
                        writer.reservePosition(id, tx, AT, wallet, money("1"), null);
                        return true;
                    } catch (final WalletEngine.PositionLimitExceededException e) {
                        return false;
                    }
                });
            }
            var successes = 0;
            for (final var result : pool.invokeAll(jobs)) {
                if (result.get()) {
                    successes++;
                }
            }
            assertEquals(10, successes);
        }
        assertNative(wallet, "10", "0", "10", "10");
    }

    @Test
    void duplicateWalletMissingReferencesAndPrecisionAreHandled() throws Exception {

        final var wallet = wallet(Currency.USD);
        assertThrows(WalletEngine.WalletIdAlreadyTakenException.class,
            () -> this.engine.createWallet(wallet, Currency.EUR, 2, "changed"));
        assertThrows(WalletEngine.NoBalanceUpdateException.class,
            () -> this.engine.depositBalance(transaction(), AT, new WalletId(next()), money("1"), null, balanceId()));
        assertThrows(WalletEngine.PositionReservationCommitFailedException.class,
            () -> this.engine.commitPositionReservation(positionId(), positionId()));
        assertThrows(ArithmeticException.class,
            () -> this.engine.depositBalance(transaction(), AT, wallet, new BigDecimal("0.00001"), null, balanceId()));
        assertThrows(IllegalArgumentException.class,
            () -> this.engine.depositBalance(transaction(), AT, wallet, money("-1"), null, balanceId()));

        final var large = "999999999999999999999999999999.9999";
        this.engine.depositBalance(transaction(), AT, wallet, money(large), "မင်္ဂလာပါ", balanceId());
        assertThrows(IllegalArgumentException.class,
            () -> this.engine.depositBalance(transaction(), AT, wallet, money("0.0001"), null, balanceId()));
        assertNative(wallet, large, "0", "0", "0");
    }

    @Test
    void zeroAmountReservationsAndIdNamespacesAreSupported() throws Exception {

        final var wallet = wallet(Currency.USD);
        final var id = next();
        this.engine.depositBalance(transaction(), AT, wallet, money("0"), null, new BalanceUpdateId(id));
        this.engine.increaseNdc(new NdcUpdateId(id), transaction(), AT, wallet, money("0"), null);
        this.engine.reservePosition(new PositionUpdateId(id), transaction(), AT, wallet, money("0"), null);
        this.engine.commitPositionReservation(positionId(), new PositionUpdateId(id));

        assertNative(wallet, "0", "0", "0", "0");
    }

    @Test
    void concurrentCommitAndRollbackResolveExactlyOnce() throws Exception {

        final var wallet = funded("10", "10");
        final var reservation = positionId();
        this.engine.reservePosition(reservation, transaction(), AT, wallet, money("10"), null);
        final var commit = positionId();
        final var rollback = positionId();
        try (final var other = open(); final var pool = Executors.newFixedThreadPool(2)) {
            final List<Callable<Boolean>> jobs = List.of(() -> {
                try {
                    this.engine.commitPositionReservation(commit, reservation);
                    return true;
                } catch (final WalletEngine.PositionReservationCommitFailedException e) {
                    return false;
                }
            }, () -> {
                try {
                    other.rollbackPositionReservation(rollback, reservation);
                    return true;
                } catch (final WalletEngine.PositionReservationRollbackFailedException e) {
                    return false;
                }
            });
            final var results = pool.invokeAll(jobs);
            final var committed = results.get(0).get();
            final var rolledBack = results.get(1).get();

            assertTrue(committed ^ rolledBack);
            assertNative(wallet, "10", committed ? "10" : "0", "0", "10");
        }
    }

    @Test
    void concurrentIdenticalRequestsReturnTheSameHistory() throws Exception {

        final var wallet = wallet(Currency.USD);
        final var update = balanceId();
        final var tx = transaction();
        try (final var other = open(); final var pool = Executors.newFixedThreadPool(8)) {
            final var jobs = new ArrayList<Callable<WalletEngine.BalanceHistory>>();
            for (var index = 0; index < 16; index++) {
                final var writer = index % 2 == 0 ? this.engine : other;
                jobs.add(() -> writer.depositBalance(tx, AT, wallet, money("7"), "same", update));
            }
            final var results = pool.invokeAll(jobs);
            final var expected = results.getFirst().get();
            for (final var result : results) {
                assertEquals(expected, result.get());
            }
        }
        assertNative(wallet, "7", "0", "0", "0");
    }

    private WalletId wallet(final Currency currency) throws Exception {

        final var wallet = new WalletId(next());
        this.engine.createWallet(wallet, currency, 2, "wallet-မင်္ဂလာပါ");
        return wallet;
    }

    private WalletId funded(final String balance, final String ndc) throws Exception {

        final var wallet = wallet(Currency.USD);
        this.engine.depositBalance(transaction(), AT, wallet, money(balance), "fund", balanceId());
        this.engine.increaseNdc(ndcId(), transaction(), AT, wallet, money(ndc), "cap");
        return wallet;
    }

    private static void assertNative(final WalletId wallet, final String balance, final String position,
                                     final String reserved, final String ndc) throws Exception {

        final var settings = settings();
        try (final var client = new Client(UInt128.asBytes(settings.clusterId()),
            settings.replicaAddresses().toArray(String[]::new))) {
            final var accounts = client.lookupAccounts(new IdBatch(WalletCodec.accountId(wallet.getId(), 1),
                WalletCodec.accountId(wallet.getId(), 2), WalletCodec.accountId(wallet.getId(), 3)));
            assertEquals(3, accounts.getLength());
            while (accounts.next()) {
                final var credits = accounts.getCreditsPosted().subtract(accounts.getDebitsPosted());
                switch (accounts.getCode()) {
                    case 1 -> assertEquals(money(balance).unscaledValue(), credits);
                    case 2 -> {
                        assertEquals(money(position).unscaledValue(), credits.negate());
                        assertEquals(money(reserved).unscaledValue(), accounts.getDebitsPending());
                    }
                    case 3 -> assertEquals(money(ndc).unscaledValue(), credits);
                    default -> throw new AssertionError("Unexpected account role");
                }
            }
        }
    }

    private static TigerBeetleWalletEngine open() {

        return new TigerBeetleWalletEngine(settings());
    }

    private static TigerBeetleWalletEngine.WalletClusterSettings settings() {

        return new TigerBeetleWalletEngine.WalletClusterSettings(
            new BigInteger(System.getProperty("tigerbeetle.cluster-id", "0")),
            List.of(System.getProperty("tigerbeetle.addresses").split(",")));
    }

    private static long next() {

        return IDS.incrementAndGet();
    }

    private static TransactionId transaction() {

        return new TransactionId(next());
    }

    private static BalanceUpdateId balanceId() {

        return new BalanceUpdateId(next());
    }

    private static PositionUpdateId positionId() {

        return new PositionUpdateId(next());
    }

    private static NdcUpdateId ndcId() {

        return new NdcUpdateId(next());
    }

    private static BigDecimal money(final String value) {

        return new BigDecimal(value).setScale(4);
    }
}
