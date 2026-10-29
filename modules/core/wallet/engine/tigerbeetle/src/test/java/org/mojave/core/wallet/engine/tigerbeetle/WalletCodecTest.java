package org.mojave.core.wallet.engine.tigerbeetle;

import org.junit.jupiter.api.Test;
import org.mojave.core.wallet.engine.tigerbeetle.store.WalletCodec;
import org.mojave.core.wallet.engine.tigerbeetle.store.WalletRecord;
import org.mojave.scheme.rule.enums.Currency;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WalletCodecTest {

    @Test
    void journalRoundTripRetainsPrecisionSignedPositionsAndUnicode() {

        final var before = new WalletRecord.State(17, Currency.USD, 2, 7,
            new BigDecimal("999999999999999999999999999999.9999"), new BigDecimal("-123.4567"),
            new BigDecimal("9.0001"), new BigDecimal("100.1234"));
        final var after = before.next(before.balance(), before.position().add(BigDecimal.ONE),
            before.reserved(), before.ndc());
        final var command = new WalletRecord.Command(WalletRecord.Action.INCREASE_POSITION,
            21, 17, 23, Instant.parse("2026-01-01T00:00:00.123456789Z"), BigDecimal.ONE,
            0, "😀".repeat(255), 0, 0);
        final var expected = new WalletRecord.Event(command, before, after);

        final var encoded = WalletCodec.encode(expected);

        assertEquals(expected, WalletCodec.decode(encoded));
        assertTrue(encoded.length < 4096);
    }

    @Test
    void monetaryConversionNeverSilentlyRoundsOrOverflows() {

        assertEquals(new BigDecimal("1.2300"), WalletRecord.amount(new BigDecimal("1.23")));
        assertEquals(new BigDecimal("-0.0001"), WalletRecord.amount(new BigDecimal("-0.0001")));
        assertThrows(ArithmeticException.class, () -> WalletRecord.amount(new BigDecimal("0.00001")));
        assertThrows(IllegalArgumentException.class, () -> WalletRecord.amount(BigDecimal.TEN.pow(30)));
    }

    @Test
    void journalRejectsUnknownVersionsTruncationAndTrailingData() {

        final var before = new WalletRecord.State(1, Currency.USD, 2, 0,
            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        final var command = new WalletRecord.Command(WalletRecord.Action.DEPOSIT, 1, 1, 1,
            Instant.EPOCH, BigDecimal.ONE, 0, null, 0, 0);
        final var encoded = WalletCodec.encode(new WalletRecord.Event(command, before,
            before.next(BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO)));
        final var unsupported = encoded.clone();
        unsupported[3] = 2;

        assertThrows(IllegalStateException.class, () -> WalletCodec.decode(unsupported));
        assertThrows(IllegalStateException.class, () -> WalletCodec.decode(Arrays.copyOf(encoded, 10)));
        assertThrows(IllegalStateException.class, () -> WalletCodec.decode(Arrays.copyOf(encoded, encoded.length + 1)));
    }

    @Test
    void persistentIdNamespacesHaveStableEncoding() {

        final var hex = HexFormat.of();
        assertEquals("0100000000000000010000000045574d", hex.formatHex(WalletCodec.eventId(1, 1)));
        assertEquals("0100000000000000010000000041574d", hex.formatHex(WalletCodec.accountId(1, 1)));
        assertNotEquals(hex.formatHex(WalletCodec.eventId(1, 1)), hex.formatHex(WalletCodec.eventId(2, 1)));
        assertNotEquals(hex.formatHex(WalletCodec.derivedId("version", 1, 2)),
            hex.formatHex(WalletCodec.derivedId("version", 2, 1)));
        assertNotEquals(hex.formatHex(WalletCodec.derivedId("version", 1, 2)),
            hex.formatHex(WalletCodec.derivedId("movement", 1, 2)));
    }

    @Test
    void retryIdentityIncludesBusinessMetadataAndFulfilmentPartner() {

        final var original = new WalletRecord.Command(WalletRecord.Action.COMMIT, 1, 2, 3,
            Instant.EPOCH, BigDecimal.ONE, 4, "settle", 5, 6);
        final var differentPartner = new WalletRecord.Command(WalletRecord.Action.COMMIT, 1, 2, 3,
            Instant.EPOCH, BigDecimal.ONE, 4, "settle", 7, 6);
        final var standalone = new WalletRecord.Command(WalletRecord.Action.COMMIT, 1, 2, 3,
            Instant.EPOCH, BigDecimal.ONE, 4, "settle", 0, 0);

        assertTrue(original.matches(original));
        assertFalse(original.matches(differentPartner));
        assertFalse(original.matches(standalone));
    }

    @Test
    void settingsCopyAddressesAndValidateClusterId() {

        final var addresses = new ArrayList<>(List.of("127.0.0.1:3000"));
        final var settings = new TigerBeetleWalletEngine.WalletClusterSettings(BigInteger.ZERO, addresses);
        addresses.clear();

        assertEquals(List.of("127.0.0.1:3000"), settings.replicaAddresses());
        assertThrows(IllegalArgumentException.class, () -> new TigerBeetleWalletEngine.WalletClusterSettings(
            BigInteger.ONE.shiftLeft(128), List.of("127.0.0.1:3000")));
        assertThrows(IllegalArgumentException.class, () -> new TigerBeetleWalletEngine.WalletClusterSettings(
            BigInteger.ZERO, List.of()));
    }
}
